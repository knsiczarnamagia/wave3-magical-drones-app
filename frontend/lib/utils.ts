export function parseDateTime(datetime: string): Date {
    // Example input: "31122023_154530" (31st December 2023, 15:45:30)
    const datePart = datetime.split('_')[0]; // "31122023"
    const timePart = datetime.split('_')[1]; // "154530"

    const day = parseInt(datePart.substring(0, 2), 10);
    const month = parseInt(datePart.substring(2, 4), 10) - 1;
    const year = parseInt(datePart.substring(4, 8), 10);

    const hours = parseInt(timePart.substring(0, 2), 10);
    const minutes = parseInt(timePart.substring(2, 4), 10);
    const seconds = parseInt(timePart.substring(4, 6), 10);

    // Create date in UTC
    const utcDate = new Date(Date.UTC(year, month, day, hours, minutes, seconds));
    return utcDate;
}

export function getFileExtension(file: File): string {
    const fileName = file.name;
    const parts = fileName.split('.');
    if (parts.length > 1) {
        return parts.pop() || '';
    }
    return '';
}
