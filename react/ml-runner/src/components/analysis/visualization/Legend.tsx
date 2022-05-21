import React from 'react';
import {scaleThreshold} from '@visx/scale';
import {LegendItem, LegendLabel, LegendThreshold,} from '@visx/legend';

const thresholdScale = scaleThreshold<number, string>({
    domain: [50, 60, 70, 80 ,90],
    range: ['#122549', 'rgb(26,44,78)', 'rgb(37,59,82)', 'rgb(66,101,113)', 'rgb(84,127,131)', '#b4fbde'],
});

const legendGlyphSize = 20;

export default function LegendChart({ events = false }: { events?: boolean })
{
    return (
            <Legend title="Legend [%]">
                <LegendThreshold scale={thresholdScale}>
                    {labels =>
                        labels.reverse().map((label, i) => (
                            <LegendItem
                                key={`legend-quantile-${i}`}
                                margin="1px 0"
                                onClick={() =>
                                {
                                    if (events) alert(`clicked: ${JSON.stringify(label)}`);
                                }}
                            >
                                <svg width={legendGlyphSize} height={legendGlyphSize}>
                                    <rect fill={label.value} width={legendGlyphSize} height={legendGlyphSize} />
                                </svg>
                                <LegendLabel align="left" margin="2px 0 0 10px">
                                    {label.text}
                                </LegendLabel>
                            </LegendItem>
                        ))
                    }
                </LegendThreshold>
            </Legend>
    );
}

function Legend({ title, children }: { title: string; children: React.ReactNode })
{
    return (
        <div className="legend">
            <div className="title">{title}</div>
            {children}
            <style>{`
        .legend {
          line-height: 0.9em;
          color: #efefef;
          font-size: 13px;
          font-family: arial;
          padding: 10px 10px;
          float: left;
          border: 1px solid rgba(255, 255, 255, 0.3);
          border-radius: 8px;
          margin: 5px 5px;
        }
        .title {
          font-size: 12px;
          margin-bottom: 10px;
          font-weight: 100;
        }
      `}</style>
        </div>
    );
}