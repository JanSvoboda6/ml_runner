import React from 'react';
import {render, screen} from "@testing-library/react";
import axios from "axios";
import {act} from 'react-dom/test-utils';
import {Router} from "react-router";
import ProjectList from "../../components/project/ProjectList";
import {createMemoryHistory} from "history";

describe('Rendering', () =>
{
    it.todo('When page is rendered and there is a network error then error message is shown');
    test('When there are none entries in the list then create new project button is shown', async () => {
        jest.spyOn(axios, 'get').mockResolvedValue({'data': []});

        await act(async () => {
                render(<Router history={createMemoryHistory()}><ProjectList/></Router>);
            }
        );

        expect(screen.getByText('Add New Project')).toBeInTheDocument();
    });

    test('When data has been fetched from the server then project entries are shown', async () => {

        jest.spyOn(axios, 'get').mockResolvedValueOnce({
            'data': [
                {
                    'id': 1,
                    'name': 'A first project'
                },
                {
                    'id': 2,
                    'name': 'A second project'
                }
            ]
        }).mockResolvedValueOnce({
            'data': [
                {
                    'id': 1,
                    'project': {
                        'id': 1
                    }
                }
            ]
        }).mockResolvedValueOnce({
            'data': [
                {
                    'id': 2,
                    'project': {
                        'id': 2
                    }
                }
            ]
        });

        await act(async () => {
                render(<Router history={createMemoryHistory()}><ProjectList/></Router>);
            }
        );

        expect(screen.getByText('A first project')).toBeInTheDocument();
        expect(screen.getByText('A second project')).toBeInTheDocument();
    });

    test('When data has not been fetched yet from the server then loading animation is shown', async () => {
        jest.spyOn(axios, 'get').mockResolvedValueOnce({
            'data': [
                {
                    'id': 1,
                    'name': 'A first project'
                },
                {
                    'id': 2,
                    'name': 'A second project'
                }
            ]
        }).mockResolvedValueOnce({
            'data': [
                {
                    'id': 1,
                    'project': {
                        'id': 1
                    }
                }
            ]
        }).mockResolvedValueOnce({
            'data': [
                {
                    'id': 2,
                    'project': {
                        'id': 2
                    }
                }
            ]
        });

        await act(async () => {
                render(<Router history={createMemoryHistory()}><ProjectList/></Router>);
                expect(screen.getByAltText('loading animation')).toBeInTheDocument();
            }
        );

        expect(screen.queryByAltText('loading animation')).not.toBeInTheDocument();
    });
});