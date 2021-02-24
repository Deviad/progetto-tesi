import React, {useEffect} from "react";
import {useDispatch} from "react-redux";
import {getSetCurrentPage} from "../../../app/appSharedSlice";
import {PageSlug} from "../../../types";


export const Home = () => {
    const dispatch = useDispatch();


    useEffect(() => {
        dispatch(getSetCurrentPage(PageSlug.HOME));
    }, [])

    return <div>This is my home page</div>

};
