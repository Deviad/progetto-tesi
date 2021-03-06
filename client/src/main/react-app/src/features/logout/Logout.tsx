import React, {useEffect} from "react";
// @ts-ignore
import {useHistory} from "react-router-dom";
import {httpGet} from "../../httpClient";
import {BASE_URL, USER_ENDPOINT} from "../../constants";
import {PagePathName, PageSlug, UseProfileFormData} from "../../types";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {utils} from "../../utils";
import {getSetCurrentPage} from "../../app/appSharedSlice";


export const Logout = () => {
    const history = useHistory();

    const accessToken = useSelector((state: RootState) => state.user.accessToken);

    const dispatch = useDispatch();

    useEffect(() => {

        const logout = async () => {
            dispatch(getSetCurrentPage(PageSlug.LOGOUT))

            await httpGet<UseProfileFormData>({
                url: `${BASE_URL}${USER_ENDPOINT}/logout`,
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                }
            })
                .then(() => utils.storage.removeItem("auth_res"))
                .then(() => history.push(PagePathName.HOME));
        }

        logout();

    }, []);

    return <div>This is a showcase of OAuth2 integration in React</div>

};
