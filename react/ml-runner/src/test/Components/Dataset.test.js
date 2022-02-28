import React from 'react';
import {render, screen} from "@testing-library/react";
import {act} from 'react-dom/test-utils';
import DatasetService from "../../components/dataset/DatasetService";
import Datasets from "../../components/dataset/Datasets";
import DatasetUtility from "../../components/dataset/DatasetUtility";

describe('Dataset rendering', () => {
    test('When Dataset is rendered then files are shown', async () => {
        jest.spyOn(DatasetService, 'getFiles').mockResolvedValue({
            'data': [
                {
                    'key': 'aRandomFile.txt'
                },
                {
                    'key': 'aRandomDirectory/'
                }
            ]
        });

        await act(async () => {
            render(
                <Datasets/>
            )
        });

        expect(screen.getByText('aRandomFile.txt')).toBeInTheDocument();
        expect(screen.getByText('aRandomDirectory')).toBeInTheDocument();
    });
});

describe('Creating files', () => {
    test('When creating new files then only unique new files are returned', () => {
        const existingFiles = [
            {
                'key': 'aaa.txt',
                'size': undefined,
                'data': undefined
            }
        ];
        const addedFiles = [
            {
                'name': 'aaa.txt',
                'size': undefined,
                'data': undefined
            },

            {
                'name': 'bbb.txt',
                'size': undefined,
                'data': undefined
            }
        ];

        const prefix = '';
        const uniqueAddedFiles = JSON.stringify(DatasetUtility.getUniqueAddedFiles(existingFiles, addedFiles, prefix));
        expect(uniqueAddedFiles).not.toMatch(/aaa.txt/i);
        expect(uniqueAddedFiles).toMatch(/bbb.txt/i);
    })
});

describe('Renaming files and folders', () => {
    test('When file is renamed then name of the file is changed', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/bbb.txt',
            }
        ];
        const oldKey = 'AAA/aaa.txt';
        const newKey = 'AAA/ccc.txt';

        const remainingFiles = JSON.stringify(DatasetUtility.renameFile(existingFiles, oldKey, newKey));
        expect(remainingFiles).toMatch('AAA/ccc.txt');
        expect(remainingFiles).not.toMatch('AAA/aaa.txt');
    })

    test('When file is renamed then modified time is changed', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            }
        ];
        const oldKey = 'AAA/aaa.txt';
        const newKey = 'AAA/bbb.txt';

        const remainingFiles = DatasetUtility.renameFile(existingFiles, oldKey, newKey);
        expect(remainingFiles[0].modified).not.toBe(undefined);
    })

    test('When file is renamed then new key has to be unique in the directory', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/bbb.txt',
            },
            {
                'key': 'AAA/ccc.txt'
            }
        ];
        const oldKey = 'AAA/aaa.txt';
        const newKey = 'AAA/ccc.txt';

        const remainingFiles = JSON.stringify(DatasetUtility.renameFile(existingFiles, oldKey, newKey));
        expect(remainingFiles).toMatch('AAA/aaa.txt');
        expect(remainingFiles).toMatch('AAA/bbb.txt');
        expect(remainingFiles).toMatch('AAA/ccc.txt');
    })

    test('When folder is renamed then folder name is changed', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            }
        ];
        const oldKey = 'AAA/';
        const newKey = 'BBB/';

        const remainingFiles = JSON.stringify(DatasetUtility.renameFolder(existingFiles, oldKey, newKey));
        expect(remainingFiles).toMatch('BBB/aaa.txt');
    })

    test('When folder is renamed then all child files and folders keys are updated', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/bbb.txt',
            },
            {
                'key': 'AAA/ccc.txt'
            },
            {
                'key': 'AAA/BBB/ddd.txt'
            }
        ];
        const oldKey = 'AAA/';
        const newKey = 'CCC/';

        const remainingFiles = JSON.stringify(DatasetUtility.renameFolder(existingFiles, oldKey, newKey));
        expect(remainingFiles).toMatch('CCC/aaa.txt');
        expect(remainingFiles).toMatch('CCC/bbb.txt');
        expect(remainingFiles).toMatch('CCC/ccc.txt');
        expect(remainingFiles).toMatch('CCC/BBB/ddd.txt');

    })

    test('When folder is renamed then new name has to be unique in the hierarchy', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'BBB/bbb.txt',
            }
        ];
        const oldKey = 'AAA/';
        const newKey = 'BBB/';

        const remainingFiles = JSON.stringify(DatasetUtility.renameFolder(existingFiles, oldKey, newKey));
        expect(remainingFiles).toMatch('AAA/aaa.txt');
        expect(remainingFiles).toMatch('BBB/bbb.txt');
    })

    test('When folder is renamed then modified time of the child files is changed', () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'CCC/ccc.txt'
            }
        ];
        const oldKey = 'AAA/';
        const newKey = 'BBB/';

        const remainingFiles = DatasetUtility.renameFolder(existingFiles, oldKey, newKey);
        expect(remainingFiles[0].modified).not.toBe(undefined);
        expect(remainingFiles[1].modified).toBe(undefined);
    })

});

describe("Deleting files and folders", () => {
    test("When deleting folder then all files of the folder are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    });

    test("When deleting folder then all child folders and files of the folder are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    })

    test("When deleting folder then parent folders and files are not deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/BBB/'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

    test("When deleting multiple folders then all child folders and files are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'BBB/bbb.txt',
            },
        ];

        const keysOfFoldersToBeDeleted = ['AAA/', 'BBB/'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    });

    test("When deleting multiple folders then parent folders and files are not deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            },
            {
                'key': 'AAA/CCC/ccc.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/BBB/', 'AAA/CCC/'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

    test("When deleting multiple folders with deleting the parent first then all folders and files are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            },
            {
                'key': 'AAA/CCC/ccc.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/', 'AAA/CCC/'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    });

    test("When deleting folders without files then all child folders are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/',
            },
            {
                'key': 'AAA/BBB/',
            },
            {
                'key': 'AAA/BBB/CCC',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/BBB/'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/'}]);
    });

    test("When deleting non exiting folders then all existing files remain", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['CCC/DDD/'];

        const remainingFiles = DatasetUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}, {'key': 'AAA/BBB/bbb.txt'}]);
    });

    test("When deleting a file then file is not present anymore", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];
        const keyOfFileTobeDeleted = ['AAA/BBB/bbb.txt'];
        const remainingFiles = DatasetUtility.deleteSelectedFiles(existingFiles, keyOfFileTobeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

    test("When deleting a multiple files then files are not present anymore", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            },
            {
                'key': 'AAA/CCC/ccc.txt'
            }
        ];
        const keyOfFileTobeDeleted = ['AAA/BBB/bbb.txt', 'AAA/CCC/ccc.txt'];
        const remainingFiles = DatasetUtility.deleteSelectedFiles(existingFiles, keyOfFileTobeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

});

describe("Downloading files and folders", () => {
});

