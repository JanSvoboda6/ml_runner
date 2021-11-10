import axios from "axios";
import { FileInformation } from "../../types";
const API_URL = "http://localhost:8080/api/dataset";


const createDirectory = (directory: FileInformation) =>
{
    console.log(directory);
    return axios.post(
        API_URL + '/createdirectory',
        directory,
        {
            headers: { 'Content-type': 'application/json; charset=utf-8' }
        }
    );
};

const uploadFiles = (files: any) => 
{
    let formData = new FormData();
    let keys: any = [];

    keys.push(files[0].key);

    let jsonLabelData = {
        'keys': keys
    };

    formData.append(
        'keys',
        new Blob([JSON.stringify(jsonLabelData)], {
            type: 'application/json'
        }));

    console.log(jsonLabelData.keys);

    for (let key of Object.keys(files))
    {
        if (key !== 'length')
        {
            formData.append('files', files[key].data);
        }
    }

    return axios.post(API_URL + '/upload', formData);
}

export default { createDirectory, uploadFiles }