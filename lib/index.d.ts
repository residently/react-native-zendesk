interface Config {
    appId: string;
    clientId: string;
    zendeskUrl: string;
}
export declare function initialize(config: Config): void;
export declare function identifyJWT(token: string): void;
export declare function identifyAnonymous(name?: string, email?: string): void;
export declare function registerWithDeviceIdentifier(deviceIdentifier: string, successCallback: (result: string) => void, errorCallback?: (result: string) => void): void;
export declare function unregisterDevice(): void;
interface HelpCenterOptions {
    hideContactSupport?: boolean;
}
export declare function showHelpCenter(options: HelpCenterOptions): void;
interface NewTicketOptions {
    tags?: string[];
}
export declare function showTicket(requestId: string): void;
export declare function refreshTicket(requestId: string, resultCallback?: () => void): void;
export declare function showNewTicket(options: NewTicketOptions): void;
export declare function showTicketList(): void;
interface Request {
    id: string;
    status: string;
    subject: string;
    updatedAt: string;
    lastComment: string;
    avatarUrls: string[];
}
export declare function getTickets(status: string[]): Promise<Request[]>;
export declare function createTicket(subject: string, desc: string, tags?: string[], attachments?: string[]): any;
export declare function uploadAttachment(path: string, mimeType: string, fileName: string): any;
export {};
