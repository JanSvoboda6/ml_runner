import axios from "axios";
const API_URL = "http://localhost:8080/api/models/savemodel";

const save = (modelName: any, firstLabel: string, secondLabel: string, filesOfFirstLabel: any, filesOfSecondLabel: any, selectedModel: any) => 
{
    axios.post(API_URL, { modelName, selectedModel })
        .then(res =>
        {
            //console.log(res);
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
        });

    //TODO Jan: Combine two posts request to one

    let formData = new FormData();
    let jsonLabelData = {
        'labels': [firstLabel, secondLabel]
    };

    for (let key of Object.keys(filesOfFirstLabel))
    {
        if (key !== 'length')
        {
            formData.append('filesOfFirstLabel', filesOfFirstLabel[key]);
        }
    }

    for (let key of Object.keys(filesOfSecondLabel))
    {
        if (key !== 'length')
        {
            formData.append('filesOfSecondLabel', filesOfSecondLabel[key]);
        }
    }

    formData.append(
        'labels',
        new Blob([JSON.stringify(jsonLabelData)], {
            type: 'application/json'
        }));

    return axios.post(API_URL + '/files', formData);
}

export default { save }