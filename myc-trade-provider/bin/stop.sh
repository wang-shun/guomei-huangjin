#!/bin/bash
# ------------------------------------------------------------------------------
# FileName:		stop.sh
# Version:		1.0.0
# Date:			2016-03-18
# Description:	provider应用关闭脚本
# Notes:		关闭脚本,应用关闭功能
# ------------------------------------------------------------------------------

# 环境变量

# 全局变量:bin相对路径,conf目录,logs目录,调试参数
RELATIVE_PATH=$(dirname $0)
OPT_PATH=$(cd ${RELATIVE_PATH}/.. && pwd)
CONF_PATH="${OPT_PATH}/conf"
LOGS_PATH="/space/logs/provider-trade"

echo "加载全局变量
    OPT_PATH=${OPT_PATH}
    CONF_PATH=${CONF_PATH}
    LOGS_PATH=${LOGS_PATH}
"

# 服务名称,服务端口,lib列表,stdout日志
SERVER_NAME=$(grep 'dubbo.application.name' ${CONF_PATH}/dubbo.properties | awk -F '=' '{print $2}' | sed 's/^\([a-z]*\).*$/\1/g')
SERVER_PORT=$(grep 'dubbo.protocol.dubbo.port' ${CONF_PATH}/dubbo.properties | awk -F '=' '{print $2}')
PID_FILE="$LOGS_PATH/${SERVER_NAME}.pid"

echo "加载应用信息
    SERVER_NAME=${SERVER_NAME}
    SERVER_PORT=${SERVER_PORT}
"

function stop ()
{
    echo "正在关闭服务${SERVER_NAME}"
    kill -9 $1
}

if [[ -f ${PID_FILE} ]]; then
    #判断PID文件是否存在
    PID=$(cat ${PID_FILE})
    stop ${PID}
    PID=$(ps -elf | grep ${CONF_PATH} | grep -v grep | awk '{print $4}')
    if [[ -n ${PID} ]]; then
        #判断进程与端口是否启动
        echo "${SERVER_NAME}服务关闭失败,PID为${PID}"
        exit 189
    else
        echo "${SERVER_NAME}已经关闭"
        /bin/rm -f ${PID_FILE}
    fi
else
    echo "${SERVER_NAME}没有启动,请先启动,如果服务进程存在请仔细检查后手动关闭进程"
fi
