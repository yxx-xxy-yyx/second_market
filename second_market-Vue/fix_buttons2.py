import pathlib

p = pathlib.Path(r'd:/二手/SecondMarket/SecondMarket-Vue/src/views/user/ProductDetail.vue')
c = p.read_text(encoding='utf-8')

# Delete button 2: AI card header button - use a unique single-line marker
marker = 'class="header-ai-btn"'
s2 = c.find(marker)
if s2 != -1:
    # Find the start of <el-button line
    line_start = c.rfind('\n', 0, s2)
    # Go back to find <el-button
    btn_start = c.rfind('<el-button', 0, s2)
    # Find the start of that line
    line_start = c.rfind('\n', 0, btn_start) + 1
    # Find </el-button> end
    e2 = c.find('</el-button>', s2) + len('</el-button>')
    c = c[:line_start] + c[e2:]
    print('Button 2 removed')
else:
    print('Button 2 not found')

p.write_text(c, encoding='utf-8')
print('done')
