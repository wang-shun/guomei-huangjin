#!/bin/bash
# ------------------------------------------------------------------------------
# FileName:		start.sh
# Version:		1.0.0
# Date:			2016-03-18
# Description:	provider应用启动脚本
# Notes:		启动脚本,应用启动功能
# ------------------------------------------------------------------------------

# 环境变量

# 全局变量:bin相对路径,conf目录,logs目录,调试参数
RELATIVE_PATH=$(dirname $0)
OPT_PATH=$(cd ${RELATIVE_PATH}/.. && pwd)
CONF_PATH="${OPT_PATH}/conf"
LIBS_PATH="${OPT_PATH}/lib"
LOGS_PATH="/space/logs/provider-trade"

echo "加载全局变量
    OPT_PATH=${OPT_PATH}
    CONF_PATH=${CONF_PATH}
    LIBS_PATH=${LIBS_PATH}
    LOGS_PATH=${LOGS_PATH}
"

# 服务名称,服务端口,lib列表,stdout日志
SERVER_NAME=$(grep 'dubbo.application.name' ${CONF_PATH}/dubbo.properties | awk -F '=' '{print $2}' | sed 's/^\([a-z]*\).*$/\1/g')
SERVER_PORT=$(grep 'dubbo.protocol.dubbo.port' ${CONF_PATH}/dubbo.properties | awk -F '=' '{print $2}')
LIB_JARS=$(ls $LIBS_PATH | grep .jar | awk '{print "'${LIBS_PATH}'/"$0}' | tr "\n" ":")
STDOUT_FILE="$LOGS_PATH/stdout.log"
PID_FILE="$LOGS_PATH/${SERVER_NAME}.pid"

echo "加载服务信息
    SERVER_NAME=${SERVER_NAME}
    SERVER_PORT=${SERVER_PORT}
    STDOUT_FILE=${STDOUT_FILE}
"

# JVM启动参数
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "

function start ()
{
    echo "正在启动服务${SERVER_NAME}"
    /usr/bin/nohup java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_PATH:$LIB_JARS com.alibaba.dubbo.container.Main > $STDOUT_FILE 2>&1 &
}

mkdir -p ${LOGS_PATH}
cd ${OPT_PATH}

if [[ -f ${PID_FILE} ]]; then
    #判断PID文件是否存在
    PID=$(cat ${PID_FILE})
    echo "${SERVER_NAME}服务已经启动,PID为${PID},请先关闭,如果该PID不存在请删除${PID_FILE}文件后重新执行"
    exit 189
else
    start
    PID=$(ps -elf | grep ${CONF_PATH} | grep -v grep | awk '{print $4}')
    if [[ -n ${PID} ]]; then
        #判断进程与端口是否启动
        echo "${SERVER_NAME}服务已经启动"
        echo "${PID}" > ${PID_FILE}
    else
        echo "${SERVER_NAME}服务启动失败"
        exit 189
    fi
fi
