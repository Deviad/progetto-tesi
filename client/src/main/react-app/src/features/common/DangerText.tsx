import React, {FC} from "react";
import {Typography} from "antd";
import Text from "antd/es/typography/Text";


export const DangerText: FC<{ children: string }> = ({children}) => {
    return <Typography><Text type="danger">{children}</Text></Typography>;
}
