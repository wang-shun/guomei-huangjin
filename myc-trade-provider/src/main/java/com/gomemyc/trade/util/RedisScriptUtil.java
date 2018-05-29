package com.gomemyc.trade.util;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisScriptUtil {
    
    // 投资脚本
    private static RedisScript<String> INVESTSCRIPT ;
    
    // 投资脚本内容
    private static final String SCRIPT_CONTENT_INVEST = "local loanId = KEYS[1]; local userId = KEYS[2]; local amount = KEYS[3]; local orderId = KEYS[4]; local currentTime = KEYS[5]; if not(loanId and string.len(loanId) > 0) then return 'INVEST_PARAM_ERROR_PRODUCTID'; end; if not(userId and string.len(userId) > 0) then return 'INVEST_PARAM_ERROR_USER'; end; local recordKey = 'PROCESSING_PRODUCT_' .. loanId .. '_USER_' .. userId; local investQueue = 'QUEUE_PRODUCT_' .. loanId; if(redis.call('exists', recordKey) == 1) then return 'INVEST_PROCESSING'; end; if not(string.find(amount, '^[0-9]+%.?[0-9]?[0-9]?$')) then return 'INVEST_AMOUNT_ONLY_TWO_DECIMAL'; end; amount = tonumber(amount); if not(amount) then return 'INVEST_PARAM_ERROR_AMOUNT'; elseif(amount <= 0) then return 'INVEST_AMOUNT_POSITIVE'; end; amount = math.floor(amount*100 + .5); local loanCacheKey = 'PRODUCT_INFO_' .. loanId; local loan = redis.call('hmget', loanCacheKey,'status','amount','investAmount','minAmount','maxAmount','stepAmount','investNum','openTime'); local status = loan[1]; local loanAmount = loan[2]; local investAmount = loan[3]; local investMinAmount = loan[4]; local investMaxAmount = loan[5]; local investStepAmount = loan[6]; local bidNumber = loan[7]; local openTime = loan[8]; if not(loan[1]) then return 'INVEST_PRODUCT_NOT_EXIST'; end; if not(status == 'OPENED' or (status == 'SCHEDULED' and openTime <= currentTime)) then if(status == 'INITIATED') then return 'INVEST_NOT_SCHEDULED'; elseif(status == 'SCHEDULED') then return 'INVEST_NOT_DATE_OPEN'; elseif(status == 'FINISHED') then return 'INVEST_ALREADY_FINISHED'; elseif(status == 'FAILED' or status == 'SETTLED' or status == 'CLEARED') then return 'INVEST_NOT_OPEN'; else return 'INVEST_NOT_OPENED_VISIBLE'; end; end; loanAmount = tonumber(loanAmount); if not(loanAmount and loanAmount > 0) then return 'PRODUCT_AMOUNT_CANNOT_BE_NEGATIVE'; end; investAmount = tonumber(investAmount); if not(investAmount and investAmount >= 0) then return 'PRODUCT_INVEST_AMOUNT_CANNOT_BE_NEGATIVE'; end; loanAmount = math.floor(loanAmount*100 + .5); investAmount = math.floor(investAmount*100 + .5); local balance = loanAmount - investAmount; if(balance <= 0) then return 'INVEST_NO_BALANCE'; end; if(balance < amount) then return 'INVEST_NO_ENOUGH_BALANCE'; end; investMinAmount = tonumber(investMinAmount); if not(investMinAmount) then return 'INVEST_MIN_AMOUNT_ERROR'; end; investMaxAmount = tonumber(investMaxAmount); if not(investMaxAmount and investMaxAmount >= 0) then return 'INVEST_MAX_AMOUNT_ERROR'; end; investStepAmount = tonumber(investStepAmount); if not(investStepAmount and investStepAmount >= 0) then return 'INVEST_STEP_AMOUNT_ERROR'; end; investMinAmount = math.floor(investMinAmount * 100 + .5); investMaxAmount = math.floor(investMaxAmount * 100 + .5); investStepAmount = math.floor(investStepAmount * 100 + .5); local realAmount = 0; if(balance < investMinAmount) then realAmount = balance; elseif(amount < investMinAmount ) then realAmount = 0; elseif(0 < investMaxAmount and investMaxAmount < amount ) then realAmount = investMaxAmount; elseif(0 < investStepAmount) then realAmount = amount - (amount - investMinAmount)% investStepAmount; else realAmount = amount; end; if(realAmount ~= amount) then return 'INVEST_INVALID_AMOUNT'; end; investAmount = investAmount + amount; bidNumber = bidNumber + 1; local integerAmount = math.floor(investAmount/100); local floatAmount = investAmount % 100; floatAmount = (floatAmount < 10 and '0' .. floatAmount or floatAmount); investAmount = integerAmount .. '.' .. floatAmount; local result = redis.call('hmset', loanCacheKey,'investAmount',investAmount,'investNum',bidNumber); if('OK' == result['ok']) then local expireResult = redis.call('setex', recordKey, 10 * 60, orderId); if('OK' ~= expireResult['ok']) then return 'INVEST_EXPIRE_SET'; end; local saddResult = redis.call('sadd', investQueue, recordKey); if(1 ~= saddResult) then return 'INVEST_QUEUE_ADD'; end; return investAmount; end; return 'INVEST_UPDATE_INVEST_FAILED';" ;
    
    // 投资失败,还原脚本
    private static RedisScript<String> RELEASESCRIPT ;
    
    // 投资失败,释放金额脚本内容
    private static final String SCRIPT_CONTENT_RELEASE = "local loanId = KEYS[1]; local userId = KEYS[2]; local amount = KEYS[3]; local orderId = KEYS[4]; local recordKey = 'PROCESSING_PRODUCT_' .. loanId .. '_USER_' .. userId; local investQueue = 'QUEUE_PRODUCT_' .. loanId; local oldOrderId = redis.call('get', recordKey); if not(oldOrderId and  oldOrderId == orderId) then return 'BID_ALREADY_RELEASED'; end; if not(string.find(amount, '^[0-9]+%.?[0-9]?[0-9]?$')) then return 'BID_AMOUNT_ONLY_TWO_DECIMAL'; end; local loanCacheKey = 'PRODUCT_INFO_' .. loanId; local loan = redis.call('hmget', loanCacheKey,'investAmount','investNum'); local investAmount = loan[1]; local bidNumber = loan[2]; amount = math.floor(tonumber(amount)*100 + .5); investAmount = math.floor(tonumber(investAmount) * 100 + .5); investAmount = investAmount - amount; bidNumber = bidNumber - 1; local integerAmount = math.floor(investAmount/100); local floatAmount = investAmount % 100; floatAmount = (floatAmount < 10 and '0' .. floatAmount or floatAmount); investAmount = integerAmount .. '.' .. floatAmount; local result = redis.call('hmset', loanCacheKey,'investAmount',investAmount,'investNum',bidNumber); if('OK' == result['ok']) then redis.call('del', recordKey); redis.call('srem', investQueue, recordKey); return investAmount; end; return 'BID_RELEASE_INVEST_FAILED';";
    
    // 投资成功,删除投资限制
    private static RedisScript<String> SUCCESSSCRIPT;
    
    // 投资成功,删除投资限制内容
    private static final String SCRIPT_CONTENT_SUCCESS = "local loanId = KEYS[1]; local userId = KEYS[2]; local orderId = KEYS[3]; local recordKey = 'PROCESSING_PRODUCT_' .. loanId .. '_USER_' .. userId; local investQueue = 'QUEUE_PRODUCT_' .. loanId; local oldOrderId = redis.call('get', recordKey); if not(oldOrderId and  oldOrderId == orderId) then return 'BID_SUCCESS_ALREADY_REMOVE'; end; local result = ''; local delResult = redis.call('del', recordKey); if(1 == delResult) then result = result .. 'BID_DELETE_SUCCESS'; else result = result .. 'BID_DELETE_FAILED'; end; local sremResult = redis.call('srem', investQueue, recordKey); if(1 == sremResult) then result = result .. '_SREM_SUCCESS' else result = result .. '_SREM_FAILED' end; return result;";
    
    /**
     * 获取投资脚本
     * @return
     * @author lujixiang
     * @date 2017年3月8日
     *
     */
    public static RedisScript<String> getInvestScript(){
        
        if (null != INVESTSCRIPT) {
            return INVESTSCRIPT ;
        }
        
        synchronized (SCRIPT_CONTENT_INVEST) {
            // 加载脚本
            if (null == INVESTSCRIPT) {
                INVESTSCRIPT = new DefaultRedisScript<String>(SCRIPT_CONTENT_INVEST, String.class);
            }
        }
        return INVESTSCRIPT;
    }
    
    /**
     * 获取释放金额脚本
     * @return
     * @author lujixiang
     * @date 2017年3月8日
     *
     */
    public static RedisScript<String> getReleaseScript(){
        
        if (null != RELEASESCRIPT) {
            return RELEASESCRIPT ;
        }
        
        synchronized (SCRIPT_CONTENT_RELEASE) {
            // 加载脚本
            if (null == RELEASESCRIPT) {
                RELEASESCRIPT = new DefaultRedisScript<String>(SCRIPT_CONTENT_RELEASE, String.class);
            }
        }
        return RELEASESCRIPT;
    }
    
    /**
     * 获取释放金额脚本
     * @return
     * @author lujixiang
     * @date 2017年3月8日
     *
     */
    public static RedisScript<String> getSuccessScript(){
        
        if (null != SUCCESSSCRIPT) {
            return SUCCESSSCRIPT ;
        }
        
        synchronized (SCRIPT_CONTENT_SUCCESS) {
            // 加载脚本
            if (null == SUCCESSSCRIPT) {
                SUCCESSSCRIPT = new DefaultRedisScript<String>(SCRIPT_CONTENT_SUCCESS, String.class);
            }
        }
        return SUCCESSSCRIPT;
    }

}
