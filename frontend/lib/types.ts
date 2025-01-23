import { z } from 'zod';

export const LoginFormSchema = z.object({
    username: z.string()
        .min(1, { message: 'Username must not be empty.' }),
    password: z.string()
        .min(1, { message: 'Password must not be empty.' }),
});

export type PostLoginFormState = {
    success: boolean,
    errors?: {
        username?: string[],
        password?: string[],
    },
    message?: string,
} | undefined;

export const RegisterFormSchema = z.object({
    username: z.string()
        .min(2, { message: 'Username must be at least 2 characters long.' })
        .max(30, { message: 'Username cannot be longer than 30 characters.' })
        .regex(/^\w+$/, { message: 'Username may only contain letters, numbers or underscores.' }),
    password: z.string()
        .min(8, { message: 'Password must be at least 8 characters long.' })
        .max(255, { message: 'Password cannot be longer than 255 characters.' })
        .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\da-zA-Z]).+$/, { message: 'Password must contain at least one: uppercase letter, lowercase letter, number and special symbol.' }),
});

export type RegisterFormState = {
    success: boolean,
    errors?: {
        username?: string[],
        password?: string[],
    },
    message?: string,
} | undefined;

export const CreateTransFormSchema = z.object({
    title: z.string()
        .min(1, { message: 'Title must not be empty.' })
        .max(50, { message: 'Title must be less than 50 characters.' }),
    description: z.string()
        .max(3000, { message: 'Description must be less than 3000 characters.' }),
    sourceImage: z.string()
        .uuid({ message: 'Invalid image UUID format.' }),
});

export type CreateTransFormState = {
    success: boolean,
    errors?: {
        title?: string[],
        description?: string[],
        sourceImage?: string[],
    },
    message?: string,
} | undefined;

export interface UploadImageResponse {
    uuid: string;
}

export interface TransformationData {
    id: string;
    title: string;
    description?: string;
    sourceImageUuid: string;
    transformedImageUuid?: string;
    startedAt?: string;
    completedAt?: string;
}
