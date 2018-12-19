/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

export const enum types {
  CREATE_WALLET = '@@rewards_panel/CREATE_WALLET',
  ON_WALLET_CREATED = '@@rewards_panel/ON_WALLET_CREATED',
  ON_WALLET_CREATE_FAILED = '@@rewards_panel/ON_WALLET_CREATE_FAILED',
  ON_TAB_ID = '@@rewards_panel/ON_TAB_ID',
  ON_TAB_RETRIEVED = '@@rewards_panel/ON_TAB_RETRIEVED',
  ON_PUBLISHER_DATA = '@@rewards_panel/ON_PUBLISHER_DATA',
  GET_WALLET_PROPERTIES = '@@rewards_panel/GET_WALLET_PROPERTIES',
  ON_WALLET_PROPERTIES = '@@rewards_panel/ON_WALLET_PROPERTIES',
  GET_CURRENT_REPORT = '@@rewards_panel/GET_CURRENT_REPORT',
  ON_CURRENT_REPORT = '@@rewards_panel/ON_CURRENT_REPORT',
  ON_NOTIFICATION_ADDED = '@@rewards_panel/ON_NOTIFICATION_ADDED',
  ON_NOTIFICATION_DELETED = '@@rewards_panel/ON_NOTIFICATION_DELETED',
  DELETE_NOTIFICATION = '@@rewards_panel/DELETE_NOTIFICATION',
  INCLUDE_IN_AUTO_CONTRIBUTION = '@@rewards_panel/INCLUDE_IN_AUTO_CONTRIBUTION',
  GET_GRANT = '@@rewards_panel/GET_GRANT'
}

// Note: This declaration must match the RewardsNotificationType enum in
// brave/components/brave_rewards/browser/rewards_notification_service.h
export const enum RewardsNotificationType {
  REWARDS_NOTIFICATION_INVALID = 0,
  REWARDS_NOTIFICATION_AUTO_CONTRIBUTE,
  REWARDS_NOTIFICATION_GRANT,
  REWARDS_NOTIFICATION_FAILED_CONTRIBUTION,
  REWARDS_NOTIFICATION_IMPENDING_CONTRIBUTION,
  REWARDS_NOTIFICATION_INSUFFICIENT_FUNDS,
  REWARDS_NOTIFICATION_BACKUP_WALLET,
}
