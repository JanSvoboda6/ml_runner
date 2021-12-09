import React, { useState, useMemo } from 'react';
import AreaClosed from '@visx/shape/lib/shapes/AreaClosed';
import { curveMonotoneX } from '@visx/curve';
import { scaleUtc, scaleLinear, scaleLog, scaleBand, ScaleInput, coerceNumber } from '@visx/scale';
import { Axis, Orientation, SharedAxisProps, AxisScale } from '@visx/axis';
import { GridRows, GridColumns } from '@visx/grid';
import { AnimatedAxis, AnimatedGridRows, AnimatedGridColumns } from '@visx/react-spring';
import { getSeededRandom } from '@visx/mock-data';

import { timeFormat } from 'd3-time-format';
import { GridRowsProps } from '@visx/grid/lib/grids/GridRows';
import { GridColumnsProps } from '@visx/grid/lib/grids/GridColumns';

export const backgroundColor = '#da7cff';
const axisColor = '#fff';
const tickLabelColor = '#fff';
export const labelColor = '#fff';
const gridColor = '#6e0fca';
const seededRandom = getSeededRandom(0.5);
const defaultMargin = {
    top: 20,
    right: 30,
    bottom: 30,
    left: 50,
};

const tickLabelProps = () =>
({
    dy: '0em',
    dx: '0em',
    fill: tickLabelColor,
    fontSize: 14,
    fontFamily: 'sans-serif',
    textAnchor: 'middle',
} as const);

const getMinMax = (vals: (number | { valueOf(): number })[]) =>
{
    const numericVals = vals.map(coerceNumber);
    return [Math.min(...numericVals), Math.max(...numericVals)];
};

export type AxisProps = {
    width: number;
    height: number;
    values: Array<number>,
    orientation: string;
    margin?: {
        top: number,
        right: number,
        bottom: number,
        left: number
    };
    dyTickLabel?: string;
    dxTickLabel?: string;
    showControls?: boolean;
};

type AnimationTrajectory = 'outside' | 'center' | 'min' | 'max' | undefined;

type AxisComponent = React.FC<
    SharedAxisProps<AxisScale> & {
        animationTrajectory: AnimationTrajectory;
    }
>;
type GridRowsComponent = React.FC<
    GridRowsProps<AxisScale> & {
        animationTrajectory: AnimationTrajectory;
    }
>;
type GridColumnsComponent = React.FC<
    GridColumnsProps<AxisScale> & {
        animationTrajectory: AnimationTrajectory;
    }
>;

export default function DefaultAxis({
    width: outerWidth = 800,
    height: outerHeight = 800,
    values: values = [],
    orientation = "bottom",
    margin = defaultMargin,
    showControls = true,
    dxTickLabel = '0em',
    dyTickLabel = '0em'
}: AxisProps)
{
    // use non-animated components if prefers-reduced-motion is set
    const prefersReducedMotionQuery =
        typeof window === 'undefined' ? false : window.matchMedia('(prefers-reduced-motion: reduce)');
    const prefersReducedMotion = !prefersReducedMotionQuery || !!prefersReducedMotionQuery.matches;
    const [useAnimatedComponents, setUseAnimatedComponents] = useState(!prefersReducedMotion);

    // in svg, margin is subtracted from total width/height
    const width = outerWidth - margin.left - margin.right;
    const height = outerHeight - margin.top - margin.bottom;
    const [dataToggle, setDataToggle] = useState(true);
    const [animationTrajectory, setAnimationTrajectory] = useState<AnimationTrajectory>('center');

    // define some types
    interface AxisDemoProps<Scale extends AxisScale> extends SharedAxisProps<Scale>
    {
        values: ScaleInput<Scale>[];
    }

    const AxisComponent: AxisComponent = useAnimatedComponents ? AnimatedAxis : Axis;
    const GridRowsComponent: GridRowsComponent = useAnimatedComponents ? AnimatedGridRows : GridRows;
    const GridColumnsComponent: GridColumnsComponent = useAnimatedComponents
        ? AnimatedGridColumns
        : GridColumns;

    const axes: AxisDemoProps<AxisScale<number>>[] = useMemo(() =>
    {
        return [
            {
                scale: scaleBand({
                    domain: values,
                    range: [0, width],
                    paddingOuter: 0,
                    paddingInner: 1,
                }),
                values: values,
                tickFormat: (v: string) => v,
                label: 'GAMMA',
            },
        ];
    }, [dataToggle, width]);

    if (width < 10) return null;

    const scalePadding = 0;
    const scaleHeight = height / axes.length - scalePadding;

    const yScale = scaleLinear({
        domain: [100, 0],
        range: [scaleHeight, 0],
    });

    return (
        <>
            <svg width={outerWidth} height={outerHeight}>
                <rect
                    x={0}
                    y={0}
                    width={outerWidth}
                    height={outerHeight}
                    fill={'url(#visx-axis-gradient)'}
                    rx={14}
                />
                <g transform={`translate(${margin.left},${margin.top})`}>
                    {axes.map(({ scale, values, label, tickFormat }, i) => (
                        <g key={`scale-${i}`} transform={`translate(0, ${i * (scaleHeight + scalePadding)})`}>
                            <AxisComponent
                                // force remount when this changes to see the animation difference
                                key={`axis-${animationTrajectory}`}
                                orientation={orientation === "bottom" ? Orientation.bottom : Orientation.left }
                                top={scaleHeight}
                                scale={scale}
                                tickFormat={tickFormat}
                                stroke={axisColor}
                                tickStroke={axisColor}
                                tickLabelProps={() => ({
                                    dy: dyTickLabel,
                                    dx: dxTickLabel,
                                    fill: tickLabelColor,
                                    fontSize: 14,
                                    fontFamily: 'sans-serif',
                                    textAnchor: 'middle',
                                })}
                                tickValues={label === 'log' || label === 'time' ? undefined : values}
                                numTicks={label === 'time' ? 6 : undefined}
                                label={label}
                                labelProps={{
                                    x: width + 30,
                                    y: -10,
                                    fill: labelColor,
                                    fontSize: 18,
                                    strokeWidth: 0,
                                    stroke: '#fff',
                                    paintOrder: 'stroke',
                                    fontFamily: 'sans-serif',
                                    textAnchor: 'start',
                                }}
                                animationTrajectory={animationTrajectory}
                            />
                        </g>
                    ))}
                </g>
            </svg>
        </>
    );
}
