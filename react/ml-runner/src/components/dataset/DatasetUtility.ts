import {FileInformation} from "../../types";
import Moment from "moment";

const getUniqueAddedFiles = (files: FileInformation[], addedFiles: File[], prefix: string): FileInformation[] => {
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
        files.map((existingFile) =>
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

export default {getUniqueAddedFiles};