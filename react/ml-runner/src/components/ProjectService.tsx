import axios from "axios";
const API_URL = "http://localhost:8080/api/saveproject";

const save = (projectName: any, files: any, selectedModel: any) => 
{
    axios.post(API_URL, { projectName, selectedModel })
        .then(res =>
        {
            console.log(res);
        })
        .catch(error =>
        {
            var message = "";
            if (error && error.response && error.response.data.message)
            {
                message = error.response.data.message;
            }
            else if (error.message)
            {
                message = error.message;
            }
            else if (error.toString())
            {
                message = error.toString();
            }
            console.log(message);
        })

    //TODO Jan: Combine two posts request to one

    let formData = new FormData();
    let jsonBodyData = { 'name': 'Hello' };
    for (let key of Object.keys(files))
    {
        if (key !== 'length')
        {
            formData.append('files', files[key]);
        }
    }
    formData.append('jsonBodyData',
        new Blob([JSON.stringify(jsonBodyData)], {
            type: 'application/json'
        }));

    axios.post(API_URL + '/files', formData)
        .then(res =>
        {
            console.log(res);
        })
        .catch(error =>
        {
            var message = "";
            if (error && error.response && error.response.data.message)
            {
                message = error.response.data.message;
            }
            else if (error.message)
            {
                message = error.message;
            }
            else if (error.toString())
            {
                message = error.toString();
            }

        });
}

export default { save }