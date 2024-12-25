#!/bin/sh
# $1 branch
# $2 debug forTest release
# $3 dev release prod

#打包
#./gradlew clean assemble$2 --stacktrace --info

rootPath=$(pwd)
server="root@10.138.60.124"
remote_dir="/usr/local/nginx/nginx-1.10.0/html/app2/android/pgp/"$1"/"$3
#上传原始包到服务器
originApkPath="${rootPath}/app/build/outputs/apk/"$2"/pgp_*.apk"
echo ${originApkPath}
ssh $server "rm -rf "$remote_dir
ssh $server "mkdir -p "$remote_dir
scp -r ${originApkPath} $server":"$remote_dir
echo "uploadOriginApkSuccess,originApkPath="${originApkPath}
echo "remote_dir="${remote_dir}
rm -f ${originApkPath}

exit

