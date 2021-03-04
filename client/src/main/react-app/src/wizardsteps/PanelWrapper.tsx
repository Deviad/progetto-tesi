import React from "react";
import {Button, Collapse} from "antd";

const {Panel} = Collapse;

const buttonClick: (activeKey: string, setActiveKey: Function, id: string) => React.MouseEventHandler<HTMLElement> =
    (activeKey, setActiveKey, id) => (evt) => {
        if(activeKey === "") {
            setActiveKey(id);
        } else if (activeKey !== "" && activeKey !== id) {
            setActiveKey(id);
        }  else {
            setActiveKey("");
        }
    };

export const PanelWrapper = (props: any) => {

    return <Panel {...props} forceRender={true} extra={
        <div onClick={e => e.stopPropagation()}>
            <Button size="small" onClick={buttonClick(props.activeKey, props.setActiveKey, props.id)}>
                Apasa aici
            </Button>
        </div>
    } />
};
