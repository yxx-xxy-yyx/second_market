import{_ as I,u as V,r as D,d as F,o as A,f as c,h as l,x as E,v,t as r,k as g,a1 as H,n as w,F as U,m as j,i as d,e as f,J as q,g as a,W as G,w as m,K as J,q as T,ad as P,s as R,T as W}from"./index-DUv_MlWU.js";const O={class:"notice-page"},Q={class:"page-container"},X={class:"page-header"},Y={class:"page-title"},Z={class:"page-desc"},ee={class:"notice-list"},te=["onClick"],ie={class:"notice-header"},le={class:"notice-left"},ne={class:"notice-info"},oe={class:"notice-title-row"},ae={class:"notice-title"},se={class:"notice-meta"},pe={class:"notice-time"},ce={class:"notice-action"},re={key:0,class:"notice-content"},de=["innerHTML"],he={key:0,class:"pagination"},ue={__name:"Notice",setup(ge){const{t:z,locale:n}=V(),_=D(!1),y=D([]),o=F({page:1,size:10,total:0}),K=e=>({system:"Bell",activity:"Flag",maintenance:"Tools"})[e]||"Bell",C=e=>{const i={system:"linear-gradient(135deg, #409eff 0%, #66b1ff 100%)",activity:"linear-gradient(135deg, #67c23a 0%, #85ce61 100%)",maintenance:"linear-gradient(135deg, #ff9800 0%, #ffc107 100%)"};return i[e]||i.system},M=e=>({system:"primary",activity:"success",maintenance:"warning"})[e]||"primary",N=e=>({system:n.value==="ko"?"시스템":"系统公告",activity:n.value==="ko"?"이벤트":"活动公告",maintenance:n.value==="ko"?"점검":"维护通知"})[e]||e,b=e=>{if(!e)return"-";const i=new Date(e),s=new Date-i,p=Math.floor(s/6e4),h=Math.floor(s/36e5),u=Math.floor(s/864e5);return p<1?n.value==="ko"?"방금 전":"刚刚":p<60?n.value==="ko"?`${p}분 전`:`${p}分钟前`:h<24?n.value==="ko"?`${h}시간 전`:`${h}小时前`:u<7?n.value==="ko"?`${u}일 전`:`${u}天前`:i.toLocaleDateString(n.value==="ko"?"ko-KR":"zh-CN",{year:"numeric",month:"2-digit",day:"2-digit"})},k=async()=>{try{_.value=!0;const e=[{id:1,title:"平台用户协议更新通知",titleKo:"플랫폼 이용약관 업데이트 안내",type:"system",content:`
          <h3>关于用户协议更新的重要通知</h3>
          <p>尊敬的用户：</p>
          <p>为了更好地保护您的权益，我们对《用户服务协议》进行了更新。主要更新内容包括：</p>
          <ol>
            <li>优化了用户隐私保护条款</li>
            <li>明确了交易纠纷处理流程</li>
            <li>新增了平台责任免责声明</li>
            <li>更新了违规行为处理规则</li>
          </ol>
          <p>更新后的协议将于<strong>2024年1月15日</strong>正式生效。如您继续使用本平台服务，即视为同意新版协议内容。</p>
          <p>如有疑问，请联系客服：service@example.com</p>
          <p style="text-align: right;">校园二手交易平台运营团队</p>
        `,contentKo:`
          <h3>이용약관 업데이트에 관한 중요 안내</h3>
          <p>안녕하세요, 사용자 여러분.</p>
          <p>사용자 여러분의 권익을 보다 효과적으로 보호하기 위해 이용약관을 업데이트하였습니다. 주요 내용은 다음과 같습니다:</p>
          <ol>
            <li>개인정보 보호 조항 최적화</li>
            <li>거래 분쟁 처리 프로세스 명확화</li>
            <li>플랫폼 책임 면책 조항 추가</li>
            <li>규정 위반 행위 처리 규칙 업데이트</li>
          </ol>
          <p>업데이트된 약관은 <strong>2024년 1월 15일</strong>부터 시행됩니다. 서비스를 계속 이용하실 경우 새로운 약관에 동의한 것으로 간주됩니다.</p>
          <p>문의사항은 고객센터(service@example.com)로 연락주시기 바랍니다.</p>
          <p style="text-align: right;">캠퍼스 중고거래 플랫폼 운영팀</p>
        `,publishTime:new Date(Date.now()-7200*1e3),expanded:!1},{id:2,title:"新春促销活动火热进行中",titleKo:"새해맞이 프로모션 진행 중",type:"activity",content:`
          <h3>新春特惠，限时抢购！</h3>
          <p>亲爱的用户，新春佳节来临之际，平台推出多款优惠商品，欢迎选购！</p>
          <h4>活动时间</h4>
          <p>2024年1月20日 - 2024年2月10日</p>
          <h4>活动内容</h4>
          <ul>
            <li>全场商品9折起</li>
            <li>满100减20，满200减50</li>
            <li>每日限时秒杀，低至5折</li>
            <li>新用户首单立减30元</li>
          </ul>
          <p>数量有限，先到先得，快来抢购吧！</p>
        `,contentKo:`
          <h3>새해 특가, 한정 판매!</h3>
          <p>안녕하세요! 새해를 맞아 다양한 할인 혜택을 준비했습니다.</p>
          <h4>이벤트 기간</h4>
          <p>2024년 1월 20일 - 2024년 2월 10일</p>
          <h4>이벤트 내용</h4>
          <ul>
            <li>전 상품 최대 10% 할인</li>
            <li>100위안 이상 구매 시 20위안, 200위안 이상 시 50위안 할인</li>
            <li>매일 한정 수량 타임세일 (최대 50% 할인)</li>
            <li>신규 사용자 첫 구매 시 30위안 즉시 할인</li>
          </ul>
          <p>한정 수량으로 진행되니 서둘러 확인해보세요!</p>
        `,publishTime:new Date(Date.now()-300*60*1e3),expanded:!1},{id:3,title:"系统维护升级通知",titleKo:"시스템 점검 및 업데이트 안내",type:"maintenance",content:`
          <h3>系统维护升级公告</h3>
          <p>尊敬的用户：</p>
          <p>为了提升系统性能和用户体验，我们将对平台进行维护升级。</p>
          <h4>维护时间</h4>
          <p><strong>2024年1月13日 凌晨2:00 - 6:00</strong></p>
          <h4>维护内容</h4>
          <ul>
            <li>优化搜索功能，提升检索速度</li>
            <li>修复已知bug</li>
            <li>升级安全防护系统</li>
            <li>优化页面加载速度</li>
          </ul>
          <p>维护期间平台将暂停服务，给您带来不便，敬请谅解。</p>
          <p>感谢您的支持与理解！</p>
        `,contentKo:`
          <h3>시스템 점검 공지</h3>
          <p>안녕하세요. 서비스 품질 향상을 위해 시스템 점검을 실시합니다.</p>
          <h4>점검 시간</h4>
          <p><strong>2024년 1월 13일 새벽 2:00 - 6:00</strong></p>
          <h4>점검 내용</h4>
          <ul>
            <li>검색 기능 최적화 및 속도 향상</li>
            <li>알려진 버그 수정</li>
            <li>보안 시스템 강화</li>
            <li>페이지 로딩 속도 개선</li>
          </ul>
          <p>점검 시간 동안 서비스 이용이 일시 중단되오니 양해 부탁드립니다.</p>
          <p>항상 저희 서비스를 이용해 주셔서 감사합니다.</p>
        `,publishTime:new Date(Date.now()-600*60*1e3),expanded:!1},{id:4,title:"平台功能升级完成",titleKo:"플랫폼 기능 업데이트 완료",type:"system",content:`
          <h3>功能升级完成通知</h3>
          <p>尊敬的用户，平台功能升级已顺利完成！</p>
          <h4>新增功能</h4>
          <ul>
            <li>AI智能推荐：根据您的浏览历史，为您推荐感兴趣的商品</li>
            <li>商品评价系统：购买后可对商品和卖家进行评价</li>
            <li>消息通知中心：实时接收订单、评价等重要消息</li>
            <li>收藏夹功能：喜欢的商品可以收藏，随时查看</li>
          </ul>
          <p>期待您体验全新的平台功能！</p>
        `,contentKo:`
          <h3>업데이트 완료 안내</h3>
          <p>안녕하세요. 새로운 기능들이 추가되었습니다!</p>
          <h4>새로운 기능</h4>
          <ul>
            <li>AI 스마트 추천: 사용자의 취향에 맞는 상품을 추천해 드립니다.</li>
            <li>상품 리뷰 시스템: 구매 후 상품과 판매자에 대한 리뷰를 남길 수 있습니다.</li>
            <li>알림 센터: 주문, 리뷰 등 중요 소식을 실시간으로 확인하세요.</li>
            <li>관심 목록: 마음에 드는 상품을 저장하고 언제든 확인하세요.</li>
          </ul>
          <p>더욱 편리해진 기능을 지금 바로 체험해보세요!</p>
        `,publishTime:new Date(Date.now()-1440*60*1e3),expanded:!1},{id:5,title:"诚信交易倡议书",titleKo:"신뢰 거래 캠페인",type:"system",content:`
          <h3>关于倡导诚信交易的倡议</h3>
          <p>亲爱的用户朋友们：</p>
          <p>为营造诚信、和谐的交易环境，我们向全体用户发出以下倡议：</p>
          <ol>
            <li>如实描述商品信息，不夸大、不隐瞒</li>
            <li>按时发货，保证商品质量</li>
            <li>文明交流，礼貌待人</li>
            <li>遵守平台规则，维护交易秩序</li>
            <li>发现问题及时沟通，理性解决纠纷</li>
          </ol>
          <p>让我们共同努力，打造一个值得信赖的校园二手交易平台！</p>
        `,contentKo:`
          <h3>신뢰할 수 있는 거래 환경을 위한 제안</h3>
          <p>안녕하세요, 사용자 여러분.</p>
          <p>건전하고 조화로운 거래 환경을 만들기 위해 다음과 같은 수칙을 제안합니다:</p>
          <ol>
            <li>상품 정보는 과장 없이 사실대로 작성해주세요.</li>
            <li>배송 시간을 준수하고 상품 품질을 보장해주세요.</li>
            <li>서로 예의를 지키며 소통해주세요.</li>
            <li>플랫폼 운영 규칙을 준수해주세요.</li>
            <li>문제 발생 시 이성적으로 소통하여 해결해주세요.</li>
          </ol>
          <p>함께 신뢰할 수 있는 캠퍼스 중고거래 플랫폼을 만들어가요!</p>
        `,publishTime:new Date(Date.now()-2880*60*1e3),expanded:!1}];y.value=e,o.total=e.length}catch(e){console.error("获取公告列表失败:",e),q.error(z("common.noData"))}finally{_.value=!1}},$=e=>{e.expanded=!e.expanded},B=e=>{o.size=e,o.page=1,k()},L=e=>{o.page=e,k()};return A(()=>{k()}),(e,i)=>{const x=f("el-icon"),s=f("el-tag"),p=f("el-empty"),h=f("el-pagination"),u=H("loading");return a(),c("div",O,[l("div",Q,[l("div",X,[l("h2",Y,r(e.$t("notice.platformNotice")),1),l("p",Z,r(g(n)==="ko"?"플랫폼 소식과 중요 안내를 확인하세요":"及时了解平台动态和重要通知"),1)]),E((a(),c("div",ee,[(a(!0),c(U,null,j(y.value,(t,S)=>(a(),c("div",{key:t.id,class:"notice-item",onClick:me=>$(t)},[l("div",ie,[l("div",le,[l("div",{class:"notice-icon",style:G({background:C(t.type)})},[d(x,{size:24},{default:m(()=>[(a(),w(J(K(t.type))))]),_:2},1024)],4),l("div",ne,[l("div",oe,[l("h3",ae,r(g(n)==="ko"&&t.titleKo?t.titleKo:t.title),1),S<3?(a(),w(s,{key:0,type:"danger",size:"small",class:"new-tag"},{default:m(()=>[T(r(g(n)==="ko"?"최신":"最新"),1)]),_:1})):v("",!0)]),l("div",se,[d(s,{type:M(t.type),size:"small"},{default:m(()=>[T(r(N(t.type)),1)]),_:2},1032,["type"]),l("span",pe,r(b(t.publishTime)),1)])])]),l("div",ce,[d(x,{size:20,class:R(["expand-icon",{expanded:t.expanded}])},{default:m(()=>[d(g(P))]),_:2},1032,["class"])])]),d(W,{name:"expand"},{default:m(()=>[t.expanded?(a(),c("div",re,[i[2]||(i[2]=l("div",{class:"content-divider"},null,-1)),l("div",{class:"content-body",innerHTML:g(n)==="ko"&&t.contentKo?t.contentKo:t.content},null,8,de)])):v("",!0)]),_:2},1024)],8,te))),128)),!_.value&&y.value.length===0?(a(),w(p,{key:0,description:e.$t("common.noData")},null,8,["description"])):v("",!0)])),[[u,_.value]]),y.value.length>0?(a(),c("div",he,[d(h,{"current-page":o.page,"onUpdate:currentPage":i[0]||(i[0]=t=>o.page=t),"page-size":o.size,"onUpdate:pageSize":i[1]||(i[1]=t=>o.size=t),"page-sizes":[5,10,20],total:o.total,layout:"total, sizes, prev, pager, next, jumper",onSizeChange:B,onCurrentChange:L},null,8,["current-page","page-size","total"])])):v("",!0)])])}}},ye=I(ue,[["__scopeId","data-v-ae09e0d5"]]);export{ye as default};
