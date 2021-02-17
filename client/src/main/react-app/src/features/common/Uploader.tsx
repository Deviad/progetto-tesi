import {UploadOutlined} from "@ant-design/icons";
import {Button, message, Upload} from "antd";
import React, {FC} from "react";
import {useState} from "react";


export const Uploader: FC = () => {
    const [fileList, updateFileList] = useState([]);
    const props = {
        fileList,
        beforeUpload: (file: File) => {
            if (file.type !== 'image/png') {
                message.error(`${file.name} is not a png file`);
            }
            return file.type === 'image/png';
        },
        onChange: (info: any)  => {
            console.log(info.fileList);
            // file.status is empty when beforeUpload return false
            updateFileList(info.fileList.filter((file: any) => !!file.status));
        },
    };

    return (
        <Upload {...props}>
            <Button icon={<UploadOutlined/>}>Upload png only</Button>
        </Upload>
    );
}
