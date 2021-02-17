import React from "react";
import {useSelector} from 'react-redux';
import {User} from "../../types";
import {RootState} from "../../app/rootReducer";
import {Avatar, Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import {Uploader} from "../common/Uploader";


const firstLetter = (arg: string) => {
    return arg.split(" ").map(str => str.substr(0, 1)).join("");
}

const UserProfile = () => {

    const user = useSelector((state: RootState) => state.user);

    console.log("User", user);
    return (
        <>
            <Row justify="start">
                <Col style={{display: "flex", alignSelf: "left", flexBasis: "20%"}}>
                    <Typography>
                        <Title level={3}>
                            Davide Pugliese
                        </Title>
                    </Typography>
                </Col>
                <Col style={{
                    display: "flex",
                    flexBasis: "80%",
                    flexDirection: "row",
                    alignItems: "center",
                    justifyContent: "flex-end"
                }}>
                    <Avatar style={{backgroundColor: "#00a2ae", verticalAlign: 'middle', marginRight: "1rem"}}
                            size="large" gap={20}>
                        {firstLetter("Davide Pugliese")}
                    </Avatar>
                    <Uploader/>
                </Col>
            </Row>

            <pre>{JSON.stringify(user, null, 2)}</pre>
        </>
    );

}
export {UserProfile};
