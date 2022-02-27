import {FileInformation} from "../../types";
import Moment from "moment";

const getUniqueAddedFiles = (existingFiles: FileInformation[], addedFiles: File[], prefix: string): FileInformation[] => {
    const newFiles: Array<FileInformation> = addedFiles.map((file) =>
    {
        let newKey = prefix
        if (prefix !== '' && prefix.substring(prefix.length - 1) !== '/')
        {
            newKey += '/'
        }
        newKey += file.name
        return {
            key: newKey,
            size: file.size,
            modified: +Moment().unix(),
            data: file
        }
    })

    const uniqueNewFiles: Array<FileInformation> = [];

    newFiles.map((newFile) =>
    {
        let fileAlreadyExists = false;
        existingFiles.map((existingFile) =>
        {
            if (existingFile.key === newFile.key)
            {
                fileAlreadyExists = true;
            }
        })
        if (!fileAlreadyExists)
        {
            uniqueNewFiles.push(newFile);
        }
    })

    return uniqueNewFiles;
}

const deleteSelectedFolders = (existingFiles: FileInformation[], keysOfFoldersToBeDeleted: string[]): FileInformation[] => {
    return existingFiles.filter(file => !shouldBeDeleted(file.key, keysOfFoldersToBeDeleted));
}

const deleteSelectedFile = (existingFiles: FileInformation[], keyOfFileToBeDeleted: string): FileInformation[] => {
    return existingFiles.filter(file => file.key !== keyOfFileToBeDeleted);
}

const renameFile = (existingFiles: FileInformation[], oldKey: string, newKey: string): FileInformation[] => {
    const unique = isUnique(existingFiles, newKey);
    let updatedFiles: FileInformation[] = [];
    existingFiles.forEach(file => {
        if(unique && file.key === oldKey){
            updatedFiles.push({
                ...file,
                key: newKey,
                modified: +Moment(),
            })
        }else{
            updatedFiles.push(file);
        }
    });
    return updatedFiles;
}

const isUnique = (existingFiles: FileInformation[], keyOfTheFile: string): boolean => {
    let unique = true;
    existingFiles.forEach(file => {
        if(file.key === keyOfTheFile)
        {
            unique = false;
            return;
        }
    })
    return unique;
}

const shouldBeDeleted = (existingFileKey: string, keysOfFoldersToBeDeleted: string[]): boolean => {
    let shouldDelete = false;
    keysOfFoldersToBeDeleted.forEach((keyOfFolderToBeDeleted) => {
        if (existingFileKey.substr(0, keyOfFolderToBeDeleted.length) === keyOfFolderToBeDeleted)
        {
            shouldDelete = true;
            return;
        }
    });
    return shouldDelete;
}

export default {getUniqueAddedFiles, deleteSelectedFolders, deleteSelectedFile, renameFile};