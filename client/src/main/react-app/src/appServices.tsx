import * as H from "history";
import {utils} from "./utils";
import {Menu} from "antd";
import {PagePathName, PageSlug} from "./types";
import React from "react";

export const renderLogInMenuButton = (history: H.History) => {
    const user = utils.storage.getItem("auth_res");
    return utils.isNotTrue(user) &&
        <Menu.Item key="logout" onClick={() => history.push("/login")}>
            Login
        </Menu.Item>;
}
export const renderLogOutMenuButton = (history: H.History) => {
    const user = utils.storage.getItem("auth_res");
    return utils.isTrue(user) &&
        <Menu.Item key={PageSlug.LOGOUT} onClick={() => history.push("/logout")}>
            Logout
        </Menu.Item>;
}
export const renderRegisterMenuButton = (history: H.History) => {
    const user = utils.storage.getItem("auth_res");
    return utils.isNotTrue(user) &&
        <Menu.Item key={PageSlug.REGISTER} onClick={() => history.push(PagePathName.REGISTER)}>
            Inregistreazate
        </Menu.Item>;
}
export const renderUserProfileButton = (history: H.History) => {
    const user = utils.storage.getItem("auth_res");
    return utils.isTrue(user) &&
        <Menu.Item key={PageSlug.USER_PROFILE} onClick={() => history.push(PagePathName.USER_PROFILE)}>
            Profil
        </Menu.Item>;
}

export const renderDashboardMenuButton = (history: H.History) => {
    const user = utils.storage.getItem("auth_res");
    return utils.isTrue(user) &&
        <Menu.Item key={PageSlug.DASHBOARD} onClick={() => history.push("/dashboard")}>
            Dashboard
        </Menu.Item>;
}

export const handleClick = (setMenuState: Function) => (e: any) => {
    setMenuState({current: e.key});
};
