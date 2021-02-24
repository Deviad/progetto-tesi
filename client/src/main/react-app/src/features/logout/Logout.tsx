import React, {useEffect} from "react";
// @ts-ignore
import { useHistory } from "react-router-dom";
import {httpGet, httpPost} from "../../httpClient";
import {BASE_URL, USER_ENDPOINT} from "../../constants";
import {MediaType, PagePathName, UseProfileFormData} from "../../types";
import {useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {utils} from "../../utils";


export const Logout = () => {
    const history = useHistory();

    const accessToken = useSelector((state: RootState) => state.user.accessToken);

    useEffect(() => {

      const logout = async ()=> {
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
