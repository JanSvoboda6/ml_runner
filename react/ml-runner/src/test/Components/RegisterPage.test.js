describe('Rendering', () =>
{
    it.todo('When form is rendered then all fields and submit button are present');
    it.todo('When form is rendered then all fields are empty');
    it.todo('WHen form is rendered then submit button is disabled');
});

describe('Submitting', () =>
{
    it.todo('When form is successfully submitted then user is redirected to the login page');
    it.todo('When submitting the form failed then user is not redirected to the login page');
    it.todo('When form is submitted and there is a problem on the server side then error message is displayed');
    it.todo('When form is submitted then POST request is sent');
    it.todo('When form has been submitted and there is an error then submit button becomes enabled');
});

describe('Validation', () =>
{
    it.todo('When form is submitted then all fields must be filled');
    it.todo('When fields are not properly filled then submit button is disabled');
    it.todo('When fields are properly filled then submit button is enabled');
    it.todo('When email is filled then email has correct format');
    it.todo('When email is filled then email must be shorter or equal to maximum email length');
    it.todo('When password is filled then password must be shorter or equal to maximum password length');
    it.todo('When email is not in a correct format then the warning message is displayed');
    it.todo('When password is not in a correct length then the warning message is displayed');
});