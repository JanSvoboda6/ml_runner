import axios from "axios";
import { FileInformation } from "../../types";
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
const API_URL = BACKEND_URL + "/api/dataset";

const getFiles = () =>
{
    return axios.get(API_URL, {headers: authorizationHeader()});
}

const createFolder = (folder: FileInformation) =>
{
    return axios.post(
        API_URL + '/folders/create',
        folder,
        {
            headers: 
            {
                'Authorization': authorizationHeader()['Authorization'],
                'Content-type': 'application/json; charset=utf-8'
            }
        }
    );
};

const uploadFiles = (files: any) => 
{
    let formData = new FormData();
    let keys: any = [];

    files.forEach(file =>
    {
        keys.push(file.key);
    });

    let jsonLabelData = {
        'keys': keys
    };

    formData.append(
        'keys',
        new Blob([JSON.stringify(jsonLabelData)], {
            type: 'application/json'
        }));

    for (let key of Object.keys(files))
    {
        if (key !== 'length')
        {
            formData.append('files', files[key].data);
        }
    }

    return axios.post(API_URL + '/upload', formData, { headers: authorizationHeader() }, );
}

const deleteFolders = (keys: string[]) => {
    return axios.post(
        API_URL + '/folders/delete',
        keys,
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const deleteFiles = (keys: string[]) => {
    return axios.post(
        API_URL + '/files/delete',
        keys,
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const moveFile = (oldKey: string, newKey: string) => {
    return axios.post(
        API_URL + '/files/move',
        {oldKey, newKey},
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const moveFolder = (oldKey: string, newKey: string) => {
    return axios.post(
        API_URL + '/folders/move',
        {oldKey, newKey},
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const download = (keys: string[]) => {
    return axios.post<any>(
        API_URL + '/download',
        keys,
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                },
            responseType: 'blob'
        }
    );
}

export default { getFiles, createFolder, uploadFiles, deleteFolders, deleteFiles, moveFile, moveFolder, download }