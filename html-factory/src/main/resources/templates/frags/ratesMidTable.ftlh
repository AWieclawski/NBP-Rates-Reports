<table>
<tr><th>Currency:</th><th>Rate:</th><th>Valid To:</th></tr>
<#list report.ratesByDate as headers>
<tr><th colspan="3">Published at: ${headers.published} in ${headers.nbpTable}</th></tr>
<#list headers.rates as exchRate> 
<tr><td>${exchRate.currency.code}</td><td>${exchRate.rate}</td><td>${exchRate.validTo}</td></tr>
</#list>
</#list>
</table>