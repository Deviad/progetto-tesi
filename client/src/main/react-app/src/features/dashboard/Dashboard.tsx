import React, {FC} from "react";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {getSetCurrentPage} from "../../app/appSharedSlice";
import {PageSlug} from "../../types";
import {ProfessorDashboard} from "./ProfesorDashboard";
import {StudentDashboard} from "./StudentDashboard";


export const Dashboard: FC = () => {

    const user = useSelector((state: RootState) => state.user)
    const dispatch = useDispatch();

    dispatch(getSetCurrentPage(PageSlug.DASHBOARD))


    return (
        <>
            {user.roles?.includes("PROFESSOR") && <ProfessorDashboard/>}

            {user.roles?.includes("STUDENT") && <StudentDashboard/>}

        </>
    );
}
