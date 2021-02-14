import React from "react";
import {useSelector} from 'react-redux';
import {User} from "../../types";
import {RootState} from "../../app/rootReducer";

const UserProfile = () => {

    const user = useSelector((state: RootState) => state.user);

    console.log("User", user);

    return (
        <div>
            User is <br />
            {JSON.stringify(user)}
        </div>
    );

}
export {UserProfile};
