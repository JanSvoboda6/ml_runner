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
})

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
describe("Renaming files and folders", () => {
});
describe("Deleting files and folders", () => {
});
describe("Downloading files and folders", () => {
});

