package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.settings.service.ServiceType;
import lombok.Data;
import lombok.Setter;

@Data
public class GeneralSettingsState {
    private String displayName = "";

    private ServiceType selectedService = ServiceType.SELF_HOSTED;

    private boolean codeCompletionsEnabled = true;

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

}
