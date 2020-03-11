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

export function createTicket(path: string) {
  return RNZendesk.createTicket(path)
}

export function uploadAttachment(path: string, mimeType: string, fileName: string) {
  return RNZendesk.uploadAttachment(path, mimeType, fileName);
}
