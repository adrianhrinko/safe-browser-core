/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "brave/components/sync_device_info/brave_device_info.h"

namespace syncer {

BraveDeviceInfo::BraveDeviceInfo(
    const std::string& guid,
    const std::string& client_name,
    const std::string& chrome_version,
    const std::string& sync_user_agent,
    const sync_pb::SyncEnums::DeviceType device_type,
    const std::string& signin_scoped_device_id,
    const std::string& manufacturer_name,
    const std::string& model_name,
    base::Time last_updated_timestamp,
    base::TimeDelta pulse_interval,
    bool send_tab_to_self_receiving_enabled,
    const base::Optional<DeviceInfo::SharingInfo>& sharing_info,
    const std::string& fcm_registration_token,
    const ModelTypeSet& interested_data_types,
    bool is_self_delete_supported,
    const std::string& audit_logs)
    : DeviceInfo(guid,
                 client_name,
                 chrome_version,
                 sync_user_agent,
                 device_type,
                 signin_scoped_device_id,
                 manufacturer_name,
                 model_name,
                 last_updated_timestamp,
                 pulse_interval,
                 send_tab_to_self_receiving_enabled,
                 sharing_info,
                 fcm_registration_token,
                 interested_data_types),
      is_self_delete_supported_(is_self_delete_supported),
      audit_logs_(audit_logs) {}

bool BraveDeviceInfo::is_self_delete_supported() const {
  return is_self_delete_supported_;
}

void BraveDeviceInfo::set_is_self_delete_supported(
    bool is_self_delete_supported) {
  is_self_delete_supported_ = is_self_delete_supported;
}

const std::string& BraveDeviceInfo::get_audit_logs() const {
  return audit_logs_;
}

void BraveDeviceInfo::set_audit_logs(
    const std::string& audit_logs) {
  audit_logs_ = audit_logs;
}

}  // namespace syncer
