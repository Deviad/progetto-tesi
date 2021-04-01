import {BASE_URL, COURSE_ENDPOINT} from "../../../constants";


export enum ListType {
    ALL_COURSES = "ALL_COURSES",
    ENROLLED_COURSES = "ENROLLED_COURSES"
}


export const getUrl = (type: ListType) =>  {
    const map = {
        ALL_COURSES() {
            return `${BASE_URL}${COURSE_ENDPOINT}/getallcourses`
        },

        ENROLLED_COURSES() {
            return `${BASE_URL}${COURSE_ENDPOINT}/enrolledcourses`
        }
    };
    return map[type]();
}
