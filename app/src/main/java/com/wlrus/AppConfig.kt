package com.wlrus

object AppConfig {
    const val APP_NAME = "WLRUS"
    const val ANG_PACKAGE = "com.wlrus"

    // URLs
    const val APP_URL = "https://github.com/2dust/v2rayNG"
    const val APP_ISSUES_URL = "https://github.com/2dust/v2rayNG/issues"
    const val APP_PRIVACY_POLICY = "https://github.com/2dust/v2rayNG/blob/master/PRIVACY_POLICY.md"
    const val APP_PROMOTION_URL = ""
    const val APP_WIKI_MODE = "https://github.com/2dust/v2rayNG/wiki/Mode"
    const val APP_API_URL = "https://api.github.com/repos/2dust/v2rayNG/releases"
    const val TG_CHANNEL_URL = "https://t.me/v2rayNG"
    const val ANDROID_PACKAGE_NAME_LIST_URL = "https://raw.githubusercontent.com/2dust/androidpackagename/master/proxy.txt"
    const val GITHUB_DOWNLOAD_URL = "https://github.com/2dust/v2rayNG/releases"
    const val IP_API_URL = "http://ip-api.com/json/"
    const val DELAY_TEST_URL = "https://www.google.com/generate_204"
    const val DELAY_TEST_URL2 = "https://www.gstatic.com/generate_204"

    // Protocols
    const val HTTP_PROTOCOL = "http://"
    const val HTTPS_PROTOCOL = "https://"
    const val SOCKS5_PROTOCOL = "socks5://"
    const val VMESS_PROTOCOL = "vmess://"
    const val VLESS_PROTOCOL = "vless://"
    const val TROJAN_PROTOCOL = "trojan://"
    const val SHADOWSOCKS_PROTOCOL = "ss://"
    const val SOCKS_PROTOCOL = "socks://"
    const val HTTP_MASKS = "http://"
    const val WIREGUARD_PROTOCOL = "wireguard://"
    const val HYSTERIA2_PROTOCOL = "hysteria2://"
    const val HYSTERIA2_PROTOCOL2 = "hy2://"

    // TLS / Stream security
    const val TLS = "tls"
    const val REALITY = "reality"
    const val NONE = "none"

    // Network / transport
    const val DEFAULT_NETWORK = "tcp"
    const val HEADER_TYPE_HTTP = "http"
    const val HEADER_TYPE_NONE = "none"
    const val DEFAULT_SECURITY = "auto"
    const val DEFAULT_LEVEL = 8
    const val PROTOCOL_FREEDOM = "freedom"
    const val PROTOCOL_BLACKHOLE = "blackhole"
    const val PROTOCOL_DNS = "dns"

    // Tags
    const val TAG = "WLRUS"
    const val TAG_DIRECT = "direct"
    const val TAG_PROXY = "proxy"
    const val TAG_BLOCKED = "block"
    const val TAG_DNS = "dns-out"
    const val TAG_DOMESTIC_DNS = "dns-domestic"
    const val TAG_FRAGMENT = "fragment"
    const val TAG_BALANCER = "balancer"

    // Network addresses
    const val LOOPBACK = "127.0.0.1"
    const val PORT_SOCKS = "10808"
    const val PORT_HTTP = 10809
    const val DEFAULT_PORT = 10808
    const val DEFAULT_PORT_HTTP = 10809
    const val PORT_DEF_DELAY_TEST = 10801

    // WireGuard
    const val WIREGUARD_LOCAL_ADDRESS_V4 = "172.16.0.2/32"
    const val WIREGUARD_LOCAL_ADDRESS_V6 = "fd01:5ca1:ab1e:80fa:ab85:1a33:42a6:fb4c/128"
    const val WIREGUARD_LOCAL_MTU = "1420"
    const val WIREGUARD_ENDPOINT = "endpoint"
    const val WIREGUARD_PUBLIC_KEY = "publicKey"
    const val WIREGUARD_PRE_SHARED_KEY = "preSharedKey"
    const val WIREGUARD_DNS = "dns"
    const val WIREGUARD_INCLUDED_APPLICATIONS = "includedApplications"
    const val WIREGUARD_PRIVATE_KEY = "privateKey"
    const val WIREGUARD_ADDRESSES = "addresses"
    const val WIREGUARD_MTU = "mtu"
    const val WIREGUARD_ALLOWED_IPS = "allowedIPs"
    const val WIREGUARD_RESERVED = "reserved"

    // Routing / geosite / geoip
    const val GEOSITE_PRIVATE = "geosite:private"
    const val GEOIP_PRIVATE = "geoip:private"
    const val GEOSITE_CN = "geosite:cn"
    const val GEOIP_CN = "geoip:cn"
    val GEO_FILES_SOURCES: List<String> = listOf("https://github.com/Loyalsoldier/v2ray-rules-dat/releases/latest/download/")
    val ROUTED_IP_LIST: List<String> = listOf("10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16")

    // Private IP ranges (for bypass)
    val PRIVATE_IP_LIST = listOf(
        "10.0.0.0/8",
        "100.64.0.0/10",
        "127.0.0.0/8",
        "169.254.0.0/16",
        "172.16.0.0/12",
        "192.0.0.0/24",
        "192.168.0.0/16",
        "198.18.0.0/15",
        "198.51.100.0/24",
        "203.0.113.0/24",
        "::1/128",
        "fc00::/7",
        "fe80::/10"
    )

    // DNS providers
    const val DNS_PROXY = "https://1.1.1.1/dns-query"
    const val DNS_VPN = "1.1.1.1"
    const val DNS_ALIDNS_DOMAIN = "dns.alidns.com"
    const val DNS_ALIDNS_ADDRESSES = "223.5.5.5"
    const val DNS_CLOUDFLARE_ONE_DOMAIN = "one.one.one.one"
    const val DNS_CLOUDFLARE_ONE_ADDRESSES = "1.1.1.1"
    const val DNS_CLOUDFLARE_DNS_COM_DOMAIN = "cloudflare-dns.com"
    const val DNS_CLOUDFLARE_DNS_COM_ADDRESSES = "1.1.1.1"
    const val DNS_CLOUDFLARE_DNS_DOMAIN = "cloudflare-dns.com"
    const val DNS_CLOUDFLARE_DNS_ADDRESSES = "1.1.1.1"
    const val DNS_DNSPOD_DOMAIN = "doh.pub"
    const val DNS_DNSPOD_ADDRESSES = "119.29.29.29"
    const val DNS_GOOGLE_DOMAIN = "dns.google"
    const val DNS_GOOGLE_ADDRESSES = "8.8.8.8"
    const val DNS_QUAD9_DOMAIN = "dns.quad9.net"
    const val DNS_QUAD9_ADDRESSES = "9.9.9.9"
    const val DNS_YANDEX_DOMAIN = "common.dot.dns.yandex.net"
    const val DNS_YANDEX_ADDRESSES = "77.88.8.8"
    const val GOOGLEAPIS_CN_DOMAIN = "googleapis.cn"
    const val GOOGLEAPIS_COM_DOMAIN = "googleapis.com"

    val DNS_AGENT = arrayOf(
        "1.1.1.1",
        "8.8.8.8",
        "8.8.4.4",
        "9.9.9.9",
        "localhost",
        "https://1.1.1.1/dns-query",
        "https://8.8.8.8/dns-query",
    )

    val DNS_DIRECT = "1.0.0.1"

    // Preferences
    const val PREF_INBOUND_SOCKS = "pref_inbound_socks"
    const val PREF_SOCKS_PORT = "pref_socks_port"
    const val PREF_HTTP_PORT = "pref_http_port"
    const val PREF_HTTP_PORT_SHORT = "pref_http_port_short"
    const val PREF_INBOUND_HTTP = "pref_inbound_http"
    const val PREF_SNIFFING_ENABLED = "pref_sniffing_enabled"
    const val PREF_FAKE_DNS_ENABLED = "pref_fake_dns_enabled"
    const val PREF_VPN_DNS = "pref_vpn_dns"
    const val PREF_DNS_HOSTS = "pref_dns_hosts"
    const val PREF_REMOTE_DNS = "pref_remote_dns"
    const val PREF_DOMESTIC_DNS = "pref_domestic_dns"
    const val PREF_DELAY_TEST = "pref_delay_test"
    const val PREF_DELAY_TEST_URL = "pref_delay_test_url"
    const val PREF_PREFER_IPV6 = "pref_prefer_ipv6"
    const val PREF_BYPASS_LAN = "pref_bypass_lan"
    const val PREF_BYPASS_CHINA = "pref_bypass_china"
    const val PREF_SPEED_ENABLED = "pref_speed_enabled"
    const val PREF_PROXY_SHARING_ENABLED = "pref_proxy_sharing_enabled"
    const val PREF_LOCAL_DNS_ENABLED = "pref_local_dns_enabled"
    const val PREF_LOCAL_DNS_PORT = "pref_local_dns_port"
    const val PREF_APPEND_HTTP_PROXY = "pref_append_http_proxy"
    const val PREF_LANGUAGE = "pref_language"
    const val PREF_UI_MODE_NIGHT = "pref_ui_mode_night"
    const val PREF_OUTBOUND_RULE_COUNTER = "pref_outbound_rule_counter"
    const val PREF_V2RAY_SERVICE_STATUS = "pref_v2ray_service_status"
    const val PREF_IS_BOOTED = "pref_is_booted"
    const val PREF_ROUTING_RULESET = "pref_routing_ruleset"
    const val PREF_ROUTING_CUSTOM = "pref_routing_custom"
    const val PREF_ROUTING_DOMAIN_STRATEGY = "pref_routing_domain_strategy"
    const val PREF_SUBSCRIPTION_AUTO_UPDATE = "pref_subscription_auto_update"
    const val SUBSCRIPTION_AUTO_UPDATE = "pref_subscription_auto_update"
    const val PREF_SUBSCRIPTION_AUTO_UPDATE_INTERVAL = "pref_subscription_auto_update_interval"
    const val SUBSCRIPTION_AUTO_UPDATE_INTERVAL = "pref_subscription_auto_update_interval"
    const val SUBSCRIPTION_DEFAULT_UPDATE_INTERVAL = "24"
    const val PREF_PER_APP_PROXY = "pref_per_app_proxy"
    const val PREF_PER_APP_PROXY_SET = "pref_per_app_proxy_set"
    const val PREF_BYPASS_APPS = "pref_bypass_apps"
    const val PREF_ALLOW_INSECURE = "pref_allow_insecure"
    const val PREF_MUX_ENABLED = "pref_mux_enabled"
    const val PREF_MUX_CONCURRENCY = "pref_mux_concurrency"
    const val PREF_MUX_XUDP_CONCURRENCY = "pref_mux_xudp_concurrency"
    const val PREF_MUX_XUDP_QUIC = "pref_mux_xudp_quic"
    const val PREF_MUXCOOL_ENABLED = "pref_muxcool_enabled"
    const val PREF_FRAGMENT_ENABLED = "pref_fragment_enabled"
    const val PREF_FRAGMENT_PACKETS = "pref_fragment_packets"
    const val PREF_FRAGMENT_LENGTH = "pref_fragment_length"
    const val PREF_FRAGMENT_INTERVAL = "pref_fragment_interval"
    const val PREF_FORWARD_IPV6 = "pref_forward_ipv6"
    const val PREF_TUN_ENABLED = "pref_tun_enabled"
    const val PREF_MODE = "pref_mode"
    const val PREF_LOGLEVEL = "pref_loglevel"
    const val PREF_OUTBOUND_DOMAIN_RESOLVE_METHOD = "pref_outbound_domain_resolve_method"
    const val PREF_VPN_BYPASS_LAN = "pref_vpn_bypass_lan"
    const val PREF_VPN_INTERFACE_ADDRESS_CONFIG_INDEX = "pref_vpn_interface_address_config_index"
    const val PREF_VPN_MTU = "pref_vpn_mtu"
    const val VPN_MTU = "9000"
    const val PREF_USE_HEV_TUNNEL = "pref_use_hev_tunnel"
    const val PREF_HEV_TUNNEL_LOGLEVEL = "pref_hev_tunnel_loglevel"
    const val PREF_HEV_TUNNEL_RW_TIMEOUT = "pref_hev_tunnel_rw_timeout"
    const val HEVTUN_RW_TIMEOUT = "60"
    const val PREF_DOUBLE_COLUMN_DISPLAY = "pref_double_column_display"
    const val PREF_CONFIRM_REMOVE = "pref_confirm_remove"
    const val PREF_IP_API_URL = "pref_ip_api_url"
    const val PREF_AUTO_REMOVE_INVALID_AFTER_TEST = "pref_auto_remove_invalid_after_test"
    const val PREF_AUTO_SORT_AFTER_TEST = "pref_auto_sort_after_test"
    const val PREF_GROUP_ALL_DISPLAY = "pref_group_all_display"
    const val PREF_GEO_FILES_SOURCES = "pref_geo_files_sources"
    const val PREF_ROUTE_ONLY_ENABLED = "pref_route_only_enabled"
    const val PREF_CHECK_UPDATE_PRE_RELEASE = "pref_check_update_pre_release"
    const val PREF_START_SCAN_IMMEDIATE = "pref_start_scan_immediate"

    // VPN mode values
    const val VPN = "1"
    const val PROXY = "0"

    // Subscription / IDs
    const val DEFAULT_SUBSCRIPTION_ID = ""
    const val CACHE_SUBSCRIPTION_ID = "CACHE_SUBSCRIPTION_ID"
    const val SUBSCRIPTION_UPDATE_TASK_NAME = "SubscriptionUpdateWorker"
    const val SUBSCRIPTION_UPDATE_CHANNEL = "subscription_update"
    const val SUBSCRIPTION_UPDATE_CHANNEL_NAME = "Subscription Updates"
    const val DIR_ASSETS = "assets"

    // Notification
    const val RAY_NG_CHANNEL_ID = "wlrus_channel"
    const val RAY_NG_CHANNEL_NAME = "WLRUS Background Service"
    const val CHANNEL_ID = "wlrus_channel"
    const val CHANNEL_NAME = "WLRUS Background Service"
    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_PENDING_INTENT_CONTENT = 0
    const val NOTIFICATION_PENDING_INTENT_STOP_V2RAY = 1
    const val NOTIFICATION_ICON_CONNECTING = android.R.drawable.stat_sys_warning
    const val NOTIFICATION_ICON_ANIM = android.R.anim.fade_in

    // Messages
    const val MSG_REGISTER_CLIENT = 1
    const val MSG_UNREGISTER_CLIENT = 2
    const val MSG_STATE_RUNNING = 11
    const val MSG_STATE_NOT_RUNNING = 12
    const val MSG_STATE_START = 13
    const val MSG_STATE_START_SUCCESS = 13
    const val MSG_STATE_START_FAILURE = 14
    const val MSG_STATE_STOP = 15
    const val MSG_STATE_STOP_SUCCESS = 15
    const val MSG_STATE_RESTART = 16
    const val MSG_STATE_SPEED_TEST = 17
    const val MSG_MEASURE_DELAY = 31
    const val MSG_MEASURE_DELAY_SUCCESS = 32
    const val MSG_MEASURE_CONFIG = 33
    const val MSG_MEASURE_CONFIG_SUCCESS = 34
    const val MSG_MEASURE_CONFIG_CANCEL = 35
    const val MSG_MEASURE_CONFIG_FINISH = 36
    const val MSG_MEASURE_CONFIG_NOTIFY = 37

    // Speed
    const val UPLINK = "uplink"
    const val DOWNLINK = "downlink"

    // Broadcasts
    const val BROADCAST_ACTION_SERVICE = "com.wlrus.action.service"
    const val BROADCAST_ACTION_ACTIVITY = "com.wlrus.action.activity"
    const val BROADCAST_ACTION_WIDGET_CLICK = "com.wlrus.action.widget_click"
    const val INTENT_SEND_REQUEST_REOPEN = "send_request_reopen"
    const val INTENT_FILTER_ACTION_TASKER = "com.wlrus.action.tasker"

    // Tasker
    const val TASKER_EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE"
    const val TASKER_EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB"
    const val TASKER_EXTRA_BUNDLE_SWITCH = "switch"
    const val TASKER_EXTRA_BUNDLE_GUID = "guid"
    const val TASKER_DEFAULT_GUID = ""

    // Subscription types
    const val SUB_TYPE_LINK = "link"
    const val SUB_TYPE_AUTO = "auto"

    // Builtin subscriptions (for BuiltinSubscriptions / SettingsManager)
    val BUILTIN_SUBSCRIPTIONS: Map<String, String> = emptyMap()
    const val FORCE_UPDATE_EXISTING = false

    // WebDAV
    const val WEBDAV_BACKUP_DIR = "WLRUS"
    const val WEBDAV_BACKUP_FILE_NAME = "wlrus_backup.zip"

    // Speedtest
    const val SPEEDTEST_MSG_TESTING = "testing"
    const val SPEEDTEST_MSG_SUCCESS = "success"
    const val SPEEDTEST_MSG_FAIL = "fail"
    const val SPEEDTEST_MSG_BEGIN = "begin"


    // Protocol string constants used by EConfigType enum
    const val VMESS        = "vmess://"
    const val VLESS        = "vless://"
    const val CUSTOM       = "custom://"
    const val SHADOWSOCKS  = "ss://"
    const val SOCKS        = "socks://"
    const val TROJAN       = "trojan://"
    const val WIREGUARD    = "wireguard://"
    const val HYSTERIA2    = "hysteria2://"
    const val HYSTERIA     = "hy2://"
    const val HTTP         = "http://"
    const val HY2          = "hy2://"
    const val PREF_PROXY_SHARING = "pref_proxy_sharing_enabled"
    // Misc
    const val LOOPER_SECURE = "WLRUS_SECURE"
    const val SUBSCRIPTION_DEFAULT_URL = ""
}
