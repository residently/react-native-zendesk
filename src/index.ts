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

export function showTicket(requestId: string) {
  RNZendesk.showTicket(requestId)
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

export function showNewTicket(options: NewTicketOptions) {
  RNZendesk.showNewTicket(options)
}

export function showTicketList() {
  RNZendesk.showTicketList()
}
