<#list modele as cs>
  **${cs.name}** <#if cs.ed??>(${cs.ed})</#if> ${cs.price} <#if (cs.percentDayChange>0)>+</#if>${cs.percentDayChange*100}%
</#list>
