#!/bin/bash
API_URL="http://localhost:8085/api/faq"
echo "开始导入剩余25条FAQ数据..."

# FAQ 11-35
curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是品系？","answer":"品系指在品种选育过程中，表现良好但尚未命名为品种的变异类型（如优良单株或株系）。它通常是品种选育的中间阶段，不得大面积推广。","keywords":"品系,品种选育,变异类型,优良单株","category":"名词解释","priority":75,"status":1}' > /dev/null && echo "✓ 11/35 品系"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是实质性派生品种（EDV）？","answer":"以下情况下，一个品种应被视为原始品种的实质性派生品种：（Ⅰ）主要来源于原始品种，或来源于本身主要来源于原始品种的品种，同时保留了由原始品种的基因型或基因型组合产生的基本特征的表达；（Ⅱ）与最初的品种有明显的区别；并且（Ⅲ）除因派生引起的性状差异外，在表达由原始品种的基因型或基因型组合产生的基本性状方面与原始品种一致。","keywords":"实质性派生品种,EDV,原始品种,基因型,UPOV","category":"名词解释","priority":70,"status":1}' > /dev/null && echo "✓ 12/35 EDV"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是技术问卷？","answer":"技术问卷是申请新品种权时需提交的关键文件之一。它是一份标准化的表格，要求申请人根据其品种的真实情况，填写一系列关于品种性状的描述，是DUS测试的重要参考和比对基础。","keywords":"技术问卷,新品种权,申请文件,品种性状,DUS测试","category":"名词解释","priority":70,"status":1}' > /dev/null && echo "✓ 13/35 技术问卷"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是强制许可？","answer":"在符合法律规定的情况下（如国家利益或公共利益需要），审批机关可以不经品种权人同意，依法授权他人实施该品种权，但需向品种权人支付合理使用费。","keywords":"强制许可,品种权,国家利益,公共利益,使用费","category":"名词解释","priority":65,"status":1}' > /dev/null && echo "✓ 14/35 强制许可"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是研究豁免？","answer":"研究豁免是品种权的一种例外限制。允许将授权品种用于培育其他新品种或以科研为目的的活动，无需经过品种权人许可。","keywords":"研究豁免,品种权,例外限制,培育,科研","category":"名词解释","priority":65,"status":1}' > /dev/null && echo "✓ 15/35 研究豁免"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是测试指南？","answer":"测试指南是由UPOV或各国审批机关制定的，针对特定植物属或种的DUS测试技术文件。它规定了测试方法、标准品种、性状表及判定标准，是进行DUS测试的权威依据。","keywords":"测试指南,UPOV,DUS测试,测试方法,标准品种","category":"名词解释","priority":65,"status":1}' > /dev/null && echo "✓ 16/35 测试指南"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是优先权？","answer":"优先权源自UPOV公约。在一个UPOV成员国首次提出申请后，在一定期限内（通常为12个月）就同一品种向其他成员国提出申请时，可以享有首次申请的日期作为在该国的申请日。","keywords":"优先权,UPOV公约,成员国,申请日,12个月","category":"名词解释","priority":60,"status":1}' > /dev/null && echo "✓ 17/35 优先权"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是形式审查？","answer":"形式审查是审批机关对申请文件进行的初步审查，主要检查文件的齐全性、格式和明显缺陷。通过形式审查，申请才会进入实质审查阶段。","keywords":"形式审查,申请文件,初步审查,文件齐全性","category":"名词解释","priority":60,"status":1}' > /dev/null && echo "✓ 18/35 形式审查"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是实质审查？","answer":"实质审查是形式审查通过后，审批机关对品种是否具备新颖性、特异性、一致性、稳定性（DUS）等授权条件进行的实质性技术审查，是决定是否授权的关键环节。","keywords":"实质审查,DUS,新颖性,特异性,一致性,稳定性,授权","category":"名词解释","priority":60,"status":1}' > /dev/null && echo "✓ 19/35 实质审查"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是授权公告？","answer":"授权公告是审批机关对符合授权条件的品种发布的官方公告。公告标志着品种权正式生效，社会公众可以知晓并尊重此项权利。","keywords":"授权公告,品种权,官方公告,生效","category":"名词解释","priority":55,"status":1}' > /dev/null && echo "✓ 20/35 授权公告"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"新品种保护期限是多久？","answer":"品种权的保护期限，自授权公告之日起，木本、藤本植物为25年，其他植物为20年。","keywords":"新品种保护期限,品种权,保护期限,25年,20年,木本植物,藤本植物","category":"名词解释","priority":55,"status":1}' > /dev/null && echo "✓ 21/35 新品种保护期限"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是近似品种？","answer":"近似品种是在DUS测试中，被选定与申请品种进行最相似比对的已知品种。通过与其在各个性状上的详细比较，来最终判定申请品种是否具备特异性。","keywords":"近似品种,DUS测试,比对,已知品种,特异性","category":"名词解释","priority":50,"status":1}' > /dev/null && echo "✓ 22/35 近似品种"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是标准品种？","answer":"标准品种是在DUS测试中，为某些难以客观描述的性状（如抗病性强度）提供具体比对尺度的已知品种。例如，用品种A代表高抗，品种B代表中抗作为标尺。","keywords":"标准品种,DUS测试,比对尺度,抗病性","category":"名词解释","priority":50,"status":1}' > /dev/null && echo "✓ 23/35 标准品种"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是分组性状？","answer":"分组性状是用于快速筛选和缩小比对范围的性状，通常是非常明显、稳定的特征（如花色、株型）。审查员先用分组性状在已知品种库中筛选出近似品种候选组。","keywords":"分组性状,筛选,比对范围,花色,株型","category":"名词解释","priority":50,"status":1}' > /dev/null && echo "✓ 24/35 分组性状"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是分子标记检测？","answer":"分子标记检测是利用DNA技术分析品种遗传差异的现代方法。目前主要作为一致性和真实性判定的辅助工具，未来在实质性派生品种（EDV）判定中潜力巨大。","keywords":"分子标记检测,DNA技术,遗传差异,一致性,EDV","category":"名词解释","priority":45,"status":1}' > /dev/null && echo "✓ 25/35 分子标记检测"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是权利用尽？","answer":"权利用尽又称首次销售原则。指经品种权人许可售出的繁殖材料，他人后续的再销售、使用等行为不构成侵权。但未经许可的再生产行为不适用权利用尽。","keywords":"权利用尽,首次销售原则,繁殖材料,侵权","category":"名词解释","priority":45,"status":1}' > /dev/null && echo "✓ 26/35 权利用尽"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是品种侵权？","answer":"品种侵权是指任何单位或个人未经品种权人许可，为商业目的对授权品种行使生产、销售、使用等排他性权利，且不属于农民特权或研究豁免范围的行为。","keywords":"品种侵权,品种权,商业目的,生产销售,农民特权,研究豁免","category":"名词解释","priority":45,"status":1}' > /dev/null && echo "✓ 27/35 品种侵权"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是品种权质押？","answer":"品种权质押是指品种权人将其品种权作为债务履行的担保，向银行或金融机构融资的一种方式。质押合同需要在审批机关办理登记才能生效。","keywords":"品种权质押,质押,担保,融资,登记","category":"名词解释","priority":40,"status":1}' > /dev/null && echo "✓ 28/35 品种权质押"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是品种权转让？","answer":"品种权转让是指品种权人将其所有权完全转移给受让方的法律行为。需要订立书面合同，并经审批机关登记和公告，转让行为自公告之日起生效。","keywords":"品种权转让,转让,所有权,书面合同,登记公告","category":"名词解释","priority":40,"status":1}' > /dev/null && echo "✓ 29/35 品种权转让"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是侵权赔偿？","answer":"侵权赔偿是指法院在认定侵权成立后，判令侵权人向品种权人支付的经济损失。计算方式可以是权利人的实际损失、侵权人的违法所得，或参照品种权许可使用费的倍数。","keywords":"侵权赔偿,经济损失,实际损失,违法所得,许可使用费","category":"名词解释","priority":40,"status":1}' > /dev/null && echo "✓ 30/35 侵权赔偿"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是非主要农作物？","answer":"非主要农作物是指稻、小麦、玉米、棉花、大豆这五种主要农作物之外的其他农作物。","keywords":"非主要农作物,主要农作物,稻,小麦,玉米,棉花,大豆","category":"名词解释","priority":35,"status":1}' > /dev/null && echo "✓ 31/35 非主要农作物"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是品种登记？","answer":"品种登记是指对列入登记目录的非主要农作物品种，在推广前需要依法向农业主管部门提出的登记申请。这是品种合法推广销售的前提。","keywords":"品种登记,非主要农作物,登记申请,推广销售","category":"名词解释","priority":35,"status":1}' > /dev/null && echo "✓ 32/35 品种登记"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是属地管理？","answer":"属地管理是品种登记申请的管理原则，一个品种只需在申请者住所所在地的一个省份申请登记。","keywords":"属地管理,品种登记,管理原则,省份","category":"名词解释","priority":30,"status":1}' > /dev/null && echo "✓ 33/35 属地管理"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是品种登记优先受理？","answer":"品种登记优先受理是指当两个以上申请者就同一品种申请登记时，优先受理最先提出的申请；同时申请的，优先受理该品种育种者的申请。","keywords":"品种登记优先受理,优先受理,育种者,申请","category":"名词解释","priority":30,"status":1}' > /dev/null && echo "✓ 34/35 品种登记优先受理"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"question":"什么是DNA指纹？","answer":"DNA指纹指具有完全个体特异的DNA多态性，其个体识别能力足以与手指指纹相媲美。","keywords":"DNA指纹,DNA多态性,个体识别,指纹","category":"名词解释","priority":30,"status":1}' > /dev/null && echo "✓ 35/35 DNA指纹"

echo ""
echo "✅ 全部35条FAQ数据导入完成！"
echo "请刷新FAQ管理页面查看 '名词解释' 分类。"
