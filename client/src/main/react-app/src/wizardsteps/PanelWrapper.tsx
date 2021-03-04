import React from "react";
import {Collapse} from "antd";

const {Panel} = Collapse;

export const PanelWrapper = (props: any) => {


    return <Panel {...props} forceRender={true} />
};
