# GOCD Jenkins Remote job trigger task plugin

The plugin expects fllowing inputs:
- Jenkins Server Url (required)
- Job Name (required)
- Job Token (optional)
- User (optional)
- Password (optional)
- Job Parameter (optional)

*NOTE:* If both `Job Token` and `user/password` was supplied then `Job Token` will be used to trigger the job.

### Job Parameters
All parameter should be supplied in the `Key=Value` format and *separated by a comma*.
```text
k1=v1,
k_2=v2
k3=v-3
k4="v4"
k5='v5'
```
 
 A multiline parameter should be included in *double quotes* and separated by a new line
 
 ```text
k1="v1=1\nv2=2"
k2="a1=3
a2=4"
```
