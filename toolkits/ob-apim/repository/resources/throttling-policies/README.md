#Following throttling key templates should be used when defining the policies

1. AllConsumers - $apiContext:$customProperty.authorizationStatus
2. CustomerPresent-Customer - $customProperty.x-fapi-customer-ip-address:$appId
3. DataRecipients - $customProperty.customerStatus:$appId
4. Unattended-CallsPerSession - $customProperty.customerStatus:$customProperty.authorizationHeader
5. Unattended-SessionCount - $apiContext:$appId:$customProperty.customerStatus
6. Unattended-SessionTPS - $customProperty.customerStatus:$customProperty.authorizationHeader 	
7. Unattended-SessionsPerDayPerCustomerPerDR - $appId:$userId:$customProperty.customerStatus
