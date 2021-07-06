import React, {useEffect} from "react";
import {useDispatch} from "react-redux";
import {getSetCurrentPage} from "../../app/appSharedSlice";
import {PageSlug} from "../../types";
import background from "../../images/ripeti-home.jpeg";
import books from "../../images/books.jpg";


export const Home = () => {
    const dispatch = useDispatch();


    useEffect(() => {
        dispatch(getSetCurrentPage(PageSlug.HOME));
    }, [])

    return (<div style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        fontSize: "50px"
    }}>
        <h1>RIPETI</h1>
        <div style={{
            backgroundImage: `url(${background})`,
            backgroundSize:  'cover',
            backgroundRepeat:   'no-repeat',
            backgroundPosition: 'center center',
            width: '100%',
            height: '300px'
        }}></div>
        <div style={{background: "#fff",
            width: '100%',
            marginTop: '40px',
            border: '2px solid #fff',
            borderRadius: '20px',
            padding: '20px',
            textAlign: 'center',
            minHeight: '400px',
            fontSize: '18px'
        }}>
            <h1>REPETITIE SPATIATA</h1>
        Prin repetition spatiata studenti pot invata mai usor concepte predate la curs. <br/>
        Ripeti reprezinta o aplicatie de suport pentru invatamant pentru a usura munca studentilor.
            <br/>
            <br/>
        <img src={books} style={{width:"50%"}} />
        </div>
    </div>);

};
