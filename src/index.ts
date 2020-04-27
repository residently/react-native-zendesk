import { NativeModules } from 'react-native'

const { RNZendesk } = NativeModules

interface Config {
  appId: string
  clientId: string
  zendeskUrl: string
}

// MARK: - Initialization

export function initialize(config: Config) {
  RNZendesk.initialize(config)
}

// MARK: - Indentification

export function identifyJWT(token: string) {
  RNZendesk.identifyJWT(token)
}

export function identifyAnonymous(name?: string, email?: string) {
  RNZendesk.identifyAnonymous(name, email)
}


// MARK: - Notifications

export function registerWithDeviceIdentifier(deviceIdentifier: string, successCallback: (result: string) => void, errorCallback?: (result: string) => void) {
  RNZendesk.registerWithDeviceIdentifier(deviceIdentifier, successCallback, errorCallback)
}

export function unregisterDevice() {
  RNZendesk.unregisterDevice()
}

// MARK: - UI Methods

interface HelpCenterOptions {
  hideContactSupport?: boolean
}

export function showHelpCenter(options: HelpCenterOptions) {
  RNZendesk.showHelpCenter(options)
}

interface NewTicketOptions {
  tags?: string[]
}

export function showTicket(requestId: string) {
  RNZendesk.showTicket(requestId)
}

export function refreshTicket(requestId: string, resultCallback = () => {}) {
  RNZendesk.refreshTicket(requestId, resultCallback)
}

export function showNewTicket(options: NewTicketOptions) {
  RNZendesk.showNewTicket(options)
}

export function showTicketList() {
  RNZendesk.showTicketList()
}

// MARK: - Ticket Methods

interface Request {
  id: string
  status: RequestStatus
  subject: string
  updatedAt: string
  lastComment: string
}

type RequestStatus = 'New' | 'Open' | 'Pending' | 'Hold' | 'Solved' | 'Closed';

export function getTickets(status: RequestStatus[]): Promise<Request[]> {
  return RNZendesk.getRequests(status.join(','));
}

export function createTicket(subject: string, desc: string, tags: string[] = [], attachments: string[] = []) {
  return RNZendesk.createTicket(subject, desc, tags, attachments)
}

export function uploadAttachment(path: string, mimeType: string, fileName: string) {
  return RNZendesk.uploadAttachment(path, mimeType, fileName);
}
