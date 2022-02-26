import React from 'react';
import {render, screen} from "@testing-library/react";
import {act} from 'react-dom/test-utils';
import DatasetService from "../../components/dataset/DatasetService";
import Datasets from "../../components/dataset/Datasets";

describe("Dataset rendering", () => {
    test("When Dataset is rendered then files are shown", async () => {
        jest.spyOn(DatasetService, "getFiles").mockResolvedValue({
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

describe("Creating files and folders", () => {
});
describe("Renaming files and folders", () => {
});
describe("Deleting files and folders", () => {
});
describe("Downloading files and folders", () => {
});

