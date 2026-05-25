import pathlib

p = pathlib.Path(r'd:/二手/SecondMarket/SecondMarket-Vue/src/views/user/ProductDetail.vue')
c = p.read_text(encoding='utf-8')

# 1. Remove the header-right div from page-header-card
hr_start = c.find('            <div class="header-right">')
hr_end = c.find('</div>
          </div>
        </el-card>', hr_start) + len('</div>')
# Find the actual end - need to find the closing of header-right div
# The structure is: header-right > price-section-header + header-stats
# Let's find it more precisely
header_right_start = c.find('            <div class="header-right">')
if header_right_start != -1:
    # Find the matching closing </div> for header-right
    # It's after header-stats closing
    stats_end = c.find('</div>
              </div>
            </div>', header_right_start)
    if stats_end != -1:
        header_right_end = stats_end + len('</div>
              </div>
            </div>')
        # Remove the header-right section
        price_section = c[header_right_start:header_right_end]
        c = c[:header_right_start] + c[header_right_end:]
        print('Removed header-right section')

# 2. Insert price and stats after thumbnail-list in image card
thumb_end_marker = '</div>
                </div>
              </div>
            </el-card>'
# Find the thumbnail-list closing
thumb_search = c.find('<div class="thumbnail-list">')
if thumb_search != -1:
    # Find the end of thumbnail-list div
    thumb_list_end = c.find('</div>
                </div>
              </div>', thumb_search)
    if thumb_list_end != -1:
        insert_pos = thumb_list_end + len('</div>')

        # Extract price and stats from the removed section
        price_html = '''
                <!-- 价格和统计信息 -->
                <div class="price-section-header">
                  <div class="current-price-header">
                    <span class="price-symbol-header">¥</span>
                    <span class="price-value-header">{{ product.price }}</span>
                  </div>
                  <div class="original-price-header" v-if="product.originalPrice">
                    <span class="original-text">{{ $t('product.originalPrice') }}</span>
                    <span class="original-value">¥{{ product.originalPrice }}</span>
                    <el-tag type="danger" size="mini" class="discount-tag-header">
                      {{ $t('product.save') }}¥{{ (product.originalPrice - product.price).toFixed(2) }}
                    </el-tag>
                  </div>
                </div>

                <div class="header-stats">
                  <div class="header-stat-item">
                    <el-icon color="#409eff"><Stamp /></el-icon>
                    <span class="stat-label">{{ $t('product.condition') }}</span>
                    <el-rate
                      v-model="product.conditionScore"
                      disabled
                      show-score
                      :max="10"
                      text-color="#ff9800"
                      size="small"
                    />
                  </div>
                  <div class="header-stat-item">
                    <el-icon color="#909399"><View /></el-icon>
                    <span class="stat-label">{{ $t('product.viewCount') }}</span>
                    <span class="stat-value">{{ product.viewCount }}</span>
                  </div>
                  <div class="header-stat-item">
                    <el-icon color="#f56c6c"><Star /></el-icon>
                    <span class="stat-label">{{ $t('dashboard.stats.favorites') }}</span>
                    <span class="stat-value">{{ product.favoriteCount || 0 }}</span>
                  </div>
                </div>'''

        c = c[:insert_pos] + price_html + c[insert_pos:]
        print('Inserted price and stats into image card')

p.write_text(c, encoding='utf-8')
print('done')
