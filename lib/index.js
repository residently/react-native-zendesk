"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.uploadAttachment = exports.createTicket = exports.getTickets = exports.showTicketList = exports.showNewTicket = exports.refreshTicket = exports.showTicket = exports.showHelpCenter = exports.unregisterDevice = exports.registerWithDeviceIdentifier = exports.identifyAnonymous = exports.identifyJWT = exports.initialize = void 0;
var react_native_1 = require("react-native");
var RNZendesk = react_native_1.NativeModules.RNZendesk;
// MARK: - Initialization
function initialize(config) {
    RNZendesk.initialize(config);
}
exports.initialize = initialize;
// MARK: - Indentification
function identifyJWT(token) {
    RNZendesk.identifyJWT(token);
}
exports.identifyJWT = identifyJWT;
function identifyAnonymous(name, email) {
    RNZendesk.identifyAnonymous(name, email);
}
exports.identifyAnonymous = identifyAnonymous;
// MARK: - Notifications
function registerWithDeviceIdentifier(deviceIdentifier, successCallback, errorCallback) {
    RNZendesk.registerWithDeviceIdentifier(deviceIdentifier, successCallback, errorCallback);
}
exports.registerWithDeviceIdentifier = registerWithDeviceIdentifier;
function unregisterDevice() {
    RNZendesk.unregisterDevice();
}
exports.unregisterDevice = unregisterDevice;
function showHelpCenter(options) {
    RNZendesk.showHelpCenter(options);
}
exports.showHelpCenter = showHelpCenter;
function showTicket(requestId) {
    RNZendesk.showTicket(requestId);
}
exports.showTicket = showTicket;
function refreshTicket(requestId, resultCallback) {
    if (resultCallback === void 0) { resultCallback = function () { }; }
    RNZendesk.refreshTicket(requestId, resultCallback);
}
exports.refreshTicket = refreshTicket;
function showNewTicket(options) {
    RNZendesk.showNewTicket(options);
}
exports.showNewTicket = showNewTicket;
function showTicketList() {
    RNZendesk.showTicketList();
}
exports.showTicketList = showTicketList;
function getTickets(status) {
    return RNZendesk.getRequests(status.join(','));
}
exports.getTickets = getTickets;
function createTicket(subject, desc, tags, attachments) {
    if (tags === void 0) { tags = []; }
    if (attachments === void 0) { attachments = []; }
    return RNZendesk.createTicket(subject, desc, tags, attachments);
}
exports.createTicket = createTicket;
function uploadAttachment(path, mimeType, fileName) {
    return RNZendesk.uploadAttachment(path, mimeType, fileName);
}
exports.uploadAttachment = uploadAttachment;
