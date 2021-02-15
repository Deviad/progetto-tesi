import React, {FC} from "react"
import {useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";


export const ErrorComponent: FC = () => {


    const error = useSelector((state: RootState) => state.shared.error);

    return <>An Error Has occurred<br/>{error}</>

}
