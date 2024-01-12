# EVS

## What is it?
EVS (External Validation Service) is a Java-based software tool which is used as a Kmehr message validator. 

A Kmehr message is a format to exchange medical information and contains structured medical data elements. It is the Belgian national standard, designed by eHealth and origins from the XML standard.

EVS can sync Kmehr messages to and from the regional vault which acts as a remote storage partner for the 6 used data-types:
- Medicationscheme
- Sumehr
- Vaccinations
- Child report
- Diary note
- Population-Based screening

### How to create a release ?

```sh
builder\release.cmd
```

### Documentation

https://github.com/smals-jy/evs/wiki 

## Who made it?
Made by IMEC vzw for Vlaamse Agentschap Zorg & Gezondheid / Vitalink.  
Updated by [Jacques Yakoub](https://github.com/jy95) to support mandatory eHealth changes in October 2023, scripts to automate the release process ,...

## Terms & Conditions
The EVS source code is licensed as open source software under the terms of the GNU Affero General Public License v3 ([AGPLv3](https://www.gnu.org/licenses/agpl-3.0.txt)).

The EVS PDF Viewer components relies on, the equally AGPLv3-licensed, iText library (v5.x) by [iText Group nv](https://itextpdf.com/en). The iText library is not distributed from this repository. Instead, in order to compile and run the EVS PDF Viewer, a copy of the iText binaries needs to be present on your machine (easily fetched for you by the Maven build tool).

If you do not agree with the terms & conditions of AGPLv3 do not use this software.
