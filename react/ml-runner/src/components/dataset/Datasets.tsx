import React, { useEffect } from 'react'
import FileBrowser from 'react-keyed-file-browser';
import Moment from 'moment';
import { Icons } from 'react-keyed-file-browser';
import '../../styles/Datasets.css';
import 'font-awesome/css/font-awesome.min.css';
import { useState } from 'react';
import axios, { AxiosResponse } from 'axios';
import { FileInformation } from '../../types';
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
                                files.push({ key: file.key });
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
        setLoaded(false);
        const folder: FileInformation = { key: key };
        setFiles(folders => [...folders, folder]);
        DatasetService.createDirectory(folder).then(() => setLoaded(true));
    }

    const handleCreateFiles = (addedFiles: File[], prefix: string) =>
    {
        const uniqueAddedFiles: FileInformation[] = DatasetUtility.getUniqueAddedFiles(files, addedFiles, prefix);
        DatasetService.uploadFiles(uniqueAddedFiles);
        setFiles(existingFiles => [...existingFiles, ...uniqueAddedFiles]);
    }

    const handleDeleteFolder = (folderKeys: string[]) =>
    {
        setFiles(DatasetUtility.deleteSelectedFolders(files, folderKeys));
    }

    // const handleDeleteFile = (fileKey) =>
    // {
    //     this.setState(state =>
    //     {
    //         const newFiles = []
    //         state.files.map((file) =>
    //         {
    //             //TODO Jan: iterate on multiple files
    //             if (file.key !== fileKey)
    //             {
    //                 newFiles.push(file)
    //             }
    //         })
    //         state.files = newFiles;
    //         return state;
    //     })
    // }

    // handleRenameFolder = (oldKey, newKey) =>
    // {
    //     this.setState(state =>
    //     {
    //         const newFiles = []
    //         state.files.map((file) =>
    //         {
    //             if (file.key.substr(0, oldKey.length) === oldKey)
    //             {
    //                 newFiles.push({
    //                     ...file,
    //                     key: file.key.replace(oldKey, newKey),
    //                     modified: +Moment(),
    //                 })
    //             } else
    //             {
    //                 newFiles.push(file)
    //             }
    //         })
    //         state.files = newFiles
    //         return state
    //     })
    // }
    // handleRenameFile = (oldKey, newKey) =>
    // {
    //     this.setState(state =>
    //     {
    //         const newFiles = []
    //         state.files.map((file) =>
    //         {
    //             if (file.key === oldKey)
    //             {
    //                 newFiles.push({
    //                     ...file,
    //                     key: newKey,
    //                     modified: +Moment(),
    //                 })
    //             } else
    //             {
    //                 newFiles.push(file)
    //             }
    //         })
    //         state.files = newFiles
    //         return state
    //     })
    // }

    // handleFileSelection(file)
    // {
    //     console.log(file.key);
    // }

    // handleNone(fileInformation)
    // {
    //     console.log(fileInformation.file);
    //     return (<div>  Selected file: {fileInformation.file.key} </div>)
    // }

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
                        <img className='dataset-loading-animation' src={loadingAnimation} alt="loadingAnimation" />
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
                    // onMoveFolder={this.handleRenameFolder}
                    // onMoveFile={this.handleRenameFile}
                    // onRenameFolder={this.handleRenameFolder}
                    // onRenameFile={this.handleRenameFile}
                        onDeleteFolder={handleDeleteFolder}
                        // onDeleteFile={(fileKey) => this.handleDeleteFile(fileKey)}
                    // onSelectFile={(file) => this.handleFileSelection(file)}
                    // detailRenderer={(fileInformation) => this.handleNone(fileInformation)}
                />

                </div>
            </FadeIn>
        </div >
    )
}

export default Datasets;