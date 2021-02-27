import React, {FC, useEffect} from "react";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {getSetCurrentPage} from "../../app/appSharedSlice";
import {PageSlug} from "../../types";
import {ProfessorDashboard} from "./ProfesorDashboard";


export const Dashboard: FC = () => {

    const user = useSelector((state: RootState) => state.user)
    const dispatch = useDispatch();

   useEffect(()=> {
       dispatch(getSetCurrentPage(PageSlug.DASHBOARD))
   }, []);


    return (
        <>
            { <ProfessorDashboard/>}

            {/*{user.roles?.includes("STUDENT") && <StudentDashboard/>}*/}

        </>
    );
}
