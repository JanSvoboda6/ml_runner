import React from 'react'
import FileBrowser from 'react-keyed-file-browser';
import Moment from 'moment';
import { Icons } from 'react-keyed-file-browser';
import '../styles/Datasets.css';
import 'font-awesome/css/font-awesome.min.css';

export default class Datasets extends React.Component
{
    state = {
        files: [
            {
                key: 'datasets/zeros/a.png',
                modified: +Moment().subtract(1, 'hours'),
                size: 1.5 * 1024 * 1024,
            },
            {
                key: 'datasets/zeros/b.png',
                modified: +Moment().subtract(1, 'hours'),
                size: 1.5 * 1024 * 1024,
            },
            {
                key: 'datasets/ones/a.png',
                modified: +Moment().subtract(3, 'days'),
                size: 545 * 1024,
            },
            {
                key: 'datasets/ones/b.png',
                modified: +Moment().subtract(3, 'days'),
                size: 545 * 1024,
            },
            {
                key: 'documents/random_document.doc',
                modified: +Moment().subtract(15, 'days'),
                size: 480 * 1024,
            },
            {
                key: 'documents/random_pdf_document.pdf',
                modified: +Moment().subtract(15, 'days'),
                size: 4.2 * 1024 * 1024,
            },
        ],
    }

    handleCreateFolder = (key) =>
    {
        this.setState(state =>
        {
            state.files = state.files.concat([{
                key: key,
            }])
            return state
        })
    }
    handleCreateFiles = (files, prefix) =>
    {
        console.log(files, prefix);
        this.setState(state =>
        {
            const newFiles = files.map((file) =>
            {
                let newKey = prefix
                if (prefix !== '' && prefix.substring(prefix.length - 1, prefix.length) !== '/')
                {
                    newKey += '/'
                }
                newKey += file.name
                return {
                    key: newKey,
                    size: file.size,
                    modified: +Moment(),
                }
            })

            const uniqueNewFiles = []
            newFiles.map((newFile) =>
            {
                let exists = false
                state.files.map((existingFile) =>
                {
                    if (existingFile.key === newFile.key)
                    {
                        exists = true
                    }
                })
                if (!exists)
                {
                    uniqueNewFiles.push(newFile)
                }
            })
            state.files = state.files.concat(uniqueNewFiles)
            return state
        })
    }
    handleRenameFolder = (oldKey, newKey) =>
    {
        this.setState(state =>
        {
            const newFiles = []
            state.files.map((file) =>
            {
                if (file.key.substr(0, oldKey.length) === oldKey)
                {
                    newFiles.push({
                        ...file,
                        key: file.key.replace(oldKey, newKey),
                        modified: +Moment(),
                    })
                } else
                {
                    newFiles.push(file)
                }
            })
            state.files = newFiles
            return state
        })
    }
    handleRenameFile = (oldKey, newKey) =>
    {
        this.setState(state =>
        {
            const newFiles = []
            state.files.map((file) =>
            {
                if (file.key === oldKey)
                {
                    newFiles.push({
                        ...file,
                        key: newKey,
                        modified: +Moment(),
                    })
                } else
                {
                    newFiles.push(file)
                }
            })
            state.files = newFiles
            return state
        })
    }

    handleDeleteFolder = (folderKey) =>
    {
        //TODO Jan: iterate on multiple folderKey
        folderKey = folderKey[0];
        this.setState(state =>
        {
            const newFiles = []
            state.files.map((file) =>
            {
                if (file.key.substr(0, folderKey.length) !== folderKey)
                {
                    newFiles.push(file)
                }
            })
            state.files = newFiles;
            return state;
        })
    }
    handleDeleteFile = (fileKey) =>
    {
        this.setState(state =>
        {
            const newFiles = []
            state.files.map((file) =>
            {
                //TODO Jan: iterate on multiple files
                if (file.key !== fileKey)
                {
                    newFiles.push(file)
                }
            })
            state.files = newFiles;
            return state;
        })
    }

    handleFileSelection(file)
    {
        console.log(file.key);
    }

    handleNone(fileInformation)
    {
        console.log(fileInformation.file);
        return (<div>  Selected file: { fileInformation.file.key } </div>)
    }

    render()
    {
        return (
            <div>
                <div className="file-editor-wrapper">
                    <FileBrowser
                        files={ this.state.files }
                        icons={ Icons.FontAwesome(4) }

                        onCreateFolder={ this.handleCreateFolder }
                        onCreateFiles={ this.handleCreateFiles }
                        onMoveFolder={ this.handleRenameFolder }
                        onMoveFile={ this.handleRenameFile }
                        onRenameFolder={ this.handleRenameFolder }
                        onRenameFile={ this.handleRenameFile }
                        onDeleteFolder={ this.handleDeleteFolder }
                        onDeleteFile={ (fileKey) => this.handleDeleteFile(fileKey) }
                        onSelectFile={ (file) => this.handleFileSelection(file) }
                        detailRenderer={ (fileInformation) => this.handleNone(fileInformation) }
                    />
                </div>
            </div>
        )
    }
}


// function Datasets()
// {

//     return (
//         <div>
//             <Navbar />
//             <div className="file-editor-wrapper">
//                 <FileBrowser
//                     files={ [
//                         {
//                             key: 'aaa.png',
//                             modified: + Moment().subtract(1, 'hours'),
//                             size: 1.5 * 1024 * 1024,
//                             color: "red"
//                         },
//                         {
//                             key: 'bbb.png',
//                             modified: + Moment().subtract(3, 'hours'),
//                             size: 1.5 * 1024 * 1024,
//                         }
//                     ]
//                     }
//                     onSelectFile={ (file) => handleFileSelection(file) }
//                     detailRenderer={ (fileInformation) => handleNone(fileInformation) } >
//                 </FileBrowser>

//             </div>
//         </div >
//     )
// }

// function handleNone(fileInformation)
// {
//     console.log(fileInformation.file);
//     return (<div>  { fileInformation.file.key } </div>)
// }
// function handleFileSelection(file)
// {
//     // console.log(file.key);
// }


// export default Datasets;