package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.settings.service.ServiceType;

public class GeneralSettingsState {
    private String displayName = "";
    private ServiceType selectedService = ServiceType.SELF_HOSTED;

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) {
            var systemUserName = System.getProperty("user.name");
            if (systemUserName == null || systemUserName.isEmpty()) {
                return "User";
            }
            return systemUserName;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ServiceType getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(ServiceType selectedService) {
        this.selectedService = selectedService;
    }
}
