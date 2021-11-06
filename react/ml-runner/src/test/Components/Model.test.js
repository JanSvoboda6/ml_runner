
describe('Rendering', () =>
{
    it.todo('When Project is rendered then all basic project information are visible');
    it.todo('When user clicks on the Project Id then user is redirected to the appropriate Project page');
});

describe('Run Handling', () =>
{
    it.todo('When run button is clicked then RunnerService#run method is called');
    it.todo('When run button is clicked then running flag is set to true');
    it.todo('When project is in running state then running icon is shown');
    it.todo('When project is in running state then running time is shown');
});

describe('Stop Handling', () =>
{
    it.todo('When stop button is clicked then RunnerService#stop method is called');
    it.todo('When stop button is clicked then running flag is set to false');
    it.todo('When project is not in running state then running icon is not shown');
});

describe('Result Handling', () =>
{
    it.todo('When result are propagated via RunnerService then results are shown');
    it.todo('When thre is a problem with running the project then error message is shown');
    it.todo('When results are present then results are passed to the Visualizer service');
});