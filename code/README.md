# GovTech Hackathon POC | govtech-poc

## Prerequisites
- Installed DDEV (https://ddev.readthedocs.io/en/stable/)

## Steps to setup the project

1. Clone the project from github
`git clone git@github.com:jarheadcore/govtech-poc.git`

2. cd (`cd govtech-poc`) into the project directory and run `ddev start` to start the project

3. Run `ddev composer install` to install the dependencies

4. Run `ddev craft install` and follow the installation steps to install Craft CMS

5. Run `ddev npm install` to install the node dependencies

6. Remove the `port` from the Dev Url `PRIMARY_SITE_URL="https://govtech-poc.test:8443"` in the .env file, it should look like `PRIMARY_SITE_URL="https://govtech-poc.test"`

6. Run `ddev npm run dev` to start the Dev Server

## Notes
- On every push to the `main` branch, a new github workflow is started to build the project and deploy it to the staging environment on Ploi.io Private Hosted Server https://ploi.io/

- If performance is really slow, check the config file in .ddev/mutagen/mutagen.yml, exclude the folder "rust" with `- "/rust"` should do the trick

- Idea is to have a working Draft/POC for the Swiss Govtech Hackathon