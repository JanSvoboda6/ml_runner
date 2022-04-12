import React, {useEffect, useState} from 'react'
import FileBrowser, {Icons} from 'react-keyed-file-browser';
import Moment from 'moment';
import '../../styles/Datasets.css';
import 'font-awesome/css/font-awesome.min.css';
import {AxiosResponse} from 'axios';
import {FileInformation} from '../../types';
import DatasetService from './DatasetService';
import loadingAnimation from "../../styles/loading_graphics.gif";
import FadeIn from 'react-fade-in';
import DatasetUtility from "./DatasetUtility";

function Datasets(props)
{
    const [isLoaded, setLoaded] = useState(false);
    const [files, setFiles] = useState<FileInformation[]>([]);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() =>
    {
        DatasetService.getFiles()
            .then(
                (res: AxiosResponse<any>) =>
                {
                    const files: Array<FileInformation> = [];
                    if (res.data)
                    {
                        res.data.forEach(file =>
                        {
                            if (file.key.endsWith('/'))
                            {
                                files.push({key: file.key});
                                return;
                            }
                            files.push(file);
                        });
                    }
                    setFiles(files);
                    setLoaded(true);
                },
                (error) =>
                {
                    setErrorMessage(error.message);
                    setLoaded(true);
                }
            )
    }, [])

    const handleCreateFolder = (key: string) =>
    {
        const folder: FileInformation = {key: key};
        setFiles(folders => [...folders, folder]);
        DatasetService.createDirectory(folder);
    }

    const handleCreateFiles = (addedFiles: File[], prefix: string) =>
    {
        for(let i = 0; i < addedFiles.length; i++)
        {
            if (!addedFiles[i].name.includes("."))
            {
                props.onWarning("Drag & Drop folder functionality is not yet supported. You can upload multiple files by dragging them on already created folder.");
                return;
            }
        }
        console.log("here");
        const uniqueAddedFiles: FileInformation[] = DatasetUtility.getUniqueAddedFiles(files, addedFiles, prefix);
        DatasetService.uploadFiles(uniqueAddedFiles).then(() => setFiles(existingFiles => [...existingFiles, ...uniqueAddedFiles]));
    }

    const handleDeleteFolders = (folderKeys: string[]) =>
    {
        DatasetService.deleteFolders(folderKeys).then( () => {
            setFiles(DatasetUtility.deleteSelectedFolders(files, folderKeys));
        });
    }

    const handleDeleteFiles = (fileKeys: string[]) =>
    {
        DatasetService.deleteFiles(fileKeys).then( () => {
            setFiles(DatasetUtility.deleteSelectedFiles(files, fileKeys));
        });
    }

    const handleMoveFile = (oldKey: string, newKey: string) =>
    {
        DatasetService.moveFile(oldKey, newKey).then(() => {
            setFiles(DatasetUtility.moveFile(files, oldKey, newKey));
        })
    }

    const handleMoveFolder = (oldKey: string, newKey: string) =>
    {
        DatasetService.moveFolder(oldKey, newKey).then(() => {
            setFiles(DatasetUtility.moveFolder(files, oldKey, newKey));
        })
    }

    const handleDownloadFile = (keys: string[]) => {
        download(keys);
    }

    const handleDownloadFolder = (keys: string[]) => {
        download(keys);
    }

    const download = (keys: string[]) => {
        DatasetService.download(keys).then(response => {
            const type = response.headers['content-type']
            const blob = new Blob([response.data], {type: type})
            const link = document.createElement('a')
            link.href = window.URL.createObjectURL(blob)
            link.download = 'files.zip'
            link.click()
        });
    }

    const handleFolderSelection = (folder) =>
    {
        if (props.handleFolderSelection)
        {
            props.handleFolderSelection(folder)
        }
    }

    if (!isLoaded)
    {
        return <FadeIn>
            <div className='loading-animation-wrapper'>
                <img className='dataset-loading-animation' src={loadingAnimation} alt="loadingAnimation"/>
            </div>
        </FadeIn>
    }
    return (
        <div>
            <FadeIn>
                <div className="file-editor-wrapper">
                    <FileBrowser
                        files={files.map(file =>
                        {
                            const modifiedTimeInUnixFormat = file.modified ? file.modified : 0;
                            if (modifiedTimeInUnixFormat !== 0)
                            {
                                const modified = Moment.duration(modifiedTimeInUnixFormat * 1000);
                                return ({
                                    key: file.key,
                                    modified: +modified,
                                    size: file.size
                                })
                            }
                            return ({
                                key: file.key
                            })
                        })}
                        icons={Icons.FontAwesome(4)}

                        onCreateFolder={handleCreateFolder}
                        onCreateFiles={handleCreateFiles}
                        onSelectFolder={(folder) => handleFolderSelection(folder)}
                        onMoveFolder={(oldKey, newKey) => handleMoveFolder(oldKey, newKey)}
                        onMoveFile={(oldKey, newKey) => handleMoveFile(oldKey, newKey)}
                        onRenameFolder={(oldKey, newKey) => handleMoveFolder(oldKey, newKey)}
                        onRenameFile={(oldKey, newKey) => handleMoveFile(oldKey, newKey)}
                        onDeleteFolder={handleDeleteFolders}
                        onDeleteFile={handleDeleteFiles}
                        onDownloadFile={handleDownloadFile}
                        onDownloadFolder={handleDownloadFolder}
                    />
                </div>
            </FadeIn>
        </div>
    )
}

export default Datasets;