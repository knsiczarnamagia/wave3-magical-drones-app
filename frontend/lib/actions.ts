'use server';

import {
    PostLoginFormState,
    LoginFormSchema,
    CreateTransFormState,
    CreateTransFormSchema,
    UploadImageResponse,
    RegisterFormSchema,
    RegisterFormState
} from './types';
import { deleteSession, saveSession } from './session';
import { redirect } from 'next/navigation';
import { callWithErrors } from './api-client';
import { getFileExtension } from './utils';

interface TokenResponse {
    token: string;
}

export async function postLogin(prevState: PostLoginFormState, formData: FormData): Promise<PostLoginFormState> {
    const validated = LoginFormSchema.safeParse({
        username: formData.get('username'),
        password: formData.get('password'),
    })
    if (!validated.success) {
        return {
            errors: validated.error.flatten().fieldErrors,
            success: false,
        }
    }

    const response = await fetch(process.env.API_URL + '/auth/token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(validated.data),
        cache: 'no-store',
    });

    if (response.ok) {
        const resBody: TokenResponse = await response.json();
        saveSession(resBody.token);
        console.debug('Login successful. Token saved: ' + resBody.token);
        await new Promise(resolve => setTimeout(resolve, 200));
        redirect('/app');
        return { message: 'Login successful.', success: true };
    } else if (response.status === 401) {
        console.debug(`Login request returned: ${response.status} status`);
        return { message: 'Invalid username or password.', success: false };
    } else {
        console.debug(`Login request returned: ${response.status} status`);
        return { message: 'Login failed.', success: false };
    }
}

export async function postRegistration(prevState: RegisterFormState, formData: FormData): Promise<RegisterFormState> {
    const validated = RegisterFormSchema.safeParse({
        username: formData.get('username'),
        password: formData.get('password'),
    })
    console.log("FORM DATA: ", formData);
    console.log("VALIDATED: ", validated);
    if (!validated.success) {
        return {
            errors: validated.error.flatten().fieldErrors,
            success: false,
            message: 'Account registration unsuccessful!',
        }
    }

    const response = await fetch(process.env.API_URL + '/account', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(validated.data),
        cache: 'no-store',
    });

    if (response.ok) {
        const resBody: TokenResponse = await response.json();
        saveSession(resBody.token);
        console.debug('Registration successful. Token saved: ' + resBody.token);
        // redirect('/app/login');
        return { message: 'Registration successful!', success: true };
    } else if (response.status === 400) {
        console.debug(`Registration request returned: ${response.status} status`);
        return { message: 'Invalid registration data.', success: false };
    } else if (response.status === 409) {
        console.debug(`Registration request returned: ${response.status} status`);
        return { errors: { username: [`Username '${validated.data.username}' is already taken.`] }, success: false };
    } else {
        console.debug(`Registration request returned: ${response.status} status`);
        return { message: 'Registration failed.', success: false };
    }
}

export async function logout() {
    console.log('LOGOUT CLICKED!');
    await deleteSession();
    redirect('/app/login?logout');
}

export async function createTransformation(
    prevState: CreateTransFormState,
    formData: FormData
): Promise<CreateTransFormState> {
    console.log("FORM DATA: ", formData);
    console.log("INSTANCEOF File: ", formData.get('sourceImage') instanceof File);

    let sourceImageUuid: string | undefined;
    const file = formData.get('sourceImage');
    if (file instanceof File) {
        console.log("FILE EXTENSION: ", getFileExtension(file));
        if (!['jpg', 'jpeg', 'png', 'tiff', 'avif', 'webp', 'bmp'].includes(getFileExtension(file))) {
            return {
                errors: {
                    sourceImage: ['Invalid file extension! Supported formats: jpg, jpeg, png, tiff, avif, webp, bmp.']
                },
                success: false
            }
        }
        sourceImageUuid = await fileUpload(file);
        console.log("UUID: ", sourceImageUuid);
    } else {
        return {
            errors: {
                sourceImage: ['Invalid file object!']
            },
            success: false
        }
    }

    const validated = CreateTransFormSchema.safeParse({
        title: formData.get('title'),
        description: formData.get('description'),
        sourceImage: sourceImageUuid
    });
    if (!validated.success) {
        return {
            errors: validated.error.flatten().fieldErrors,
            success: false,
        };
    }

    const response = await callWithErrors('/transform', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(validated.data),
    });

    if (response.ok) {
        console.debug('Transformation created successfully');
        return { message: 'Transformation created successfully.', success: true };
    } else {
        console.debug('Transformation creation failed');
        return { message: 'Transformation creation failed.', success: false };
    }
}

/**
 * Sends a request to upload a file to an S3 bucket.
 * 
 * @param file 
 * @returns a promise with image UUID if successfully uploaded
 */
async function fileUpload(file: File): Promise<string | undefined> {
    console.log(file);

    const formData = new FormData();
    formData.append('sourceImg', file);
    try {
        const response = await callWithErrors('/image', {
            method: 'POST',
            body: formData,
        });

        if (!response.ok) {
            return undefined;
        }

        const result: UploadImageResponse = await response.json();
        console.log('Upload success:', result);
        return result.uuid;
    } catch (error) {
        console.error('Error uploading:', error);
        return undefined;
    }
}

export async function makeInference(transformationId: number): Promise<void> {
    try {
        const response = await callWithErrors('/inference', {
            method: 'POST',
            headers: {
                'Accept': 'text/event-stream',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ transformationId: transformationId }),
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const reader = response.body?.getReader();
        if (!reader) throw new Error('No reader available');

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            const text = new TextDecoder().decode(value);
            const lines = text.split('\n').filter(line => line.trim() !== '');

            for (const line of lines) {
                if (line.startsWith('data: ')) {
                    const eventData = line.slice(6);
                    console.log('SSE Event:', eventData);

                    if (eventData.includes('"status":"complete"')) {
                        console.log('Generation complete');
                        break;
                    }
                }
            }
        }
    } catch (error) {
        console.error('SSE Error event:', error);
    }
}